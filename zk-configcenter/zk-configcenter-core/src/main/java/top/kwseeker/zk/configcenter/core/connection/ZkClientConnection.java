package top.kwseeker.zk.configcenter.core.connection;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 与ZK服务端的连接
 * 1） 建立连接实例
 * 可拓展：会话管理、ACL
 * 2） 节点增删改查
 * 3） 注册节点监听
 */
@Slf4j
public class ZkClientConnection {

    //ZK服务端节点
    private final String servers;
    //session超时时间s
    private final int sessionTimeout;
    private volatile ZooKeeper zk;
    //连接关闭状态
    private final AtomicBoolean closed = new AtomicBoolean(false);
    //会话处理
    private final AtomicLong zkSessionId = new AtomicLong();
    private final List<ZkSessionListener> sessionListeners = new CopyOnWriteArrayList<>();


    public ZkClientConnection(String servers, int sessionTimeout) throws IOException, InterruptedException {
        this.servers = servers;
        this.sessionTimeout = sessionTimeout;
        connectZk();
    }

    /*---------------------连接建立----------------------*/

    private void connectZk() throws InterruptedException, IOException {
        final CountDownLatch connectionLatch = new CountDownLatch(1);
        final CountDownLatch assignLatch = new CountDownLatch(1);

        if (zk != null) {
            zk.close();
            zk = null;
        }
        zk = new ZooKeeper(servers, sessionTimeout, event -> {
            log.debug("event: " + JSONObject.toJSONString(event));

            if (closed.get()) {
                return;
            }

            try {
                sessionEvent(assignLatch, connectionLatch, event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        });
        assignLatch.countDown();
        connectionLatch.await();
    }

    private void sessionEvent(final CountDownLatch assignLatch,
                              final CountDownLatch connectionLatch,
                              final WatchedEvent event) throws InterruptedException, IOException {
        assignLatch.await();

        switch (event.getState()) {
            case SyncConnected: {   //会话成功建立、节点增删改
                long newSessionId = zk.getSessionId();
                long oldSessionId = zkSessionId.getAndSet(newSessionId);

                if (oldSessionId != newSessionId) {     //如果建立的是新连接的话，执行所有会话监听回调
                    for (ZkSessionListener listener : sessionListeners) {
                        try {
                            listener.onSessionCreated(this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                connectionLatch.countDown();
                break;
            }
            case Expired: {         //会话超时
                //会话超时，重新创建一个连接
                connectZk();
                break;
            }
            default:                //其他：断开连接、认证失败
                // Disconnected -- zookeeper library will handle reconnects
                break;
        }
    }

    /*---------------------节点增删改查----------------------*/

    public void createPersistentNode(String nodePath, String data) {
        try {
            zk.create(nodePath, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deleteData(String path, Watcher dataGetter, ZkDataListener listener) throws KeeperException, InterruptedException {
        listener.onDataDeleted(path);
        if (zk.exists(path, dataGetter) != null) {
            // Node was re-created by the time we called zk.exist
            updateData(path, dataGetter, listener);
        }
    }

    private void updateData(String path, Watcher dataGetter, ZkDataListener listener) throws InterruptedException, KeeperException {
        try {
            listener.onDataChanged(path, zk.getData(path, dataGetter, null));
        } catch (KeeperException e) {
            deleteData(path, dataGetter, listener);
        }
    }

    public String readData(String path, boolean returnNullIfNotExist) {
        try {
            byte[] data = zk.getData(path, false, null);
            return new String(data);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeData(String nodePath, String defaultValue) {
        byte[] data = defaultValue.getBytes();
        try {
            zk.setData(nodePath, data, -1);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*---------------------注册节点监听----------------------*/

    public void watchNode(final String path, final ZkDataListener listener) {
        final Watcher dataGetter = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (closed.get()) {
                    return;
                }
                Event.EventType eventType = event.getType();
                switch (eventType) {
                    case NodeDataChanged:
                        try {
                            updateData(path, this, listener);
                        } catch (InterruptedException | KeeperException e) {
                            e.printStackTrace();
                        }
                        break;
                    case NodeDeleted:
                        try {
                            deleteData(path, this, listener);
                        } catch (InterruptedException | KeeperException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
    }

    //订阅子节点变化
    //递归订阅全部节点变化
}
