package top.kwseeker.zk.server;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BrokerClient {

    private static final Logger LOG = LoggerFactory.getLogger(BrokerClient.class);

    public static final String ROOT = "/lockimpl/task";
    private static final String zkHost = "localhost:2181";

    private static CuratorFramework createZkClient(String zkHost) {
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .connectString(zkHost)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        zkClient.start();
        return zkClient;
    }

    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool();

        for (int i = 0; i < 3; i++) {
            service.submit(() -> {
                CuratorFramework zkClient = createZkClient(zkHost);
                //子节点状态缓存
                PathChildrenCache childrenCache = new PathChildrenCache(zkClient, ROOT, true);
                //线程任务完成后释放资源
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    zkClient.close();
                }));
                try {
                    //childrenCache.start();
                    childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);   //调试时发现启动时已经存在的节点会触发CHILD_ADDED
                                                                                            //加上这个参数在start执行执行之前先执行rebuild的方法，而rebuild的方法不会发出任何事件通知。
                } catch (Exception e) {
                    LOG.error("children cache start failed");
                    e.printStackTrace();
                    return;
                }
                //通过子节点状态缓存添加状态变化监听
                childrenCache.getListenable().addListener((client, event) -> {
                    switch (event.getType()) {
                        case CHILD_ADDED:
                            LOG.info("child add, node path: {}", event.getData().getPath());

                            String path = event.getData().getPath();
                            //byte[] nodeData = event.getData().getData();
                            //获取锁（这里的锁就是创建文件成功）
                            boolean tryAcquireLock = false, canReleaseLock =false;
                            try {
                                zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(path + "/lock");
                                tryAcquireLock = true;
                                canReleaseLock = true;
                            } catch (Exception e) {
                                LOG.error("create lock file failed, so lock failed");
                                return;
                            }
                            //这里放临界操作
                            if(tryAcquireLock) {
                                byte[] nodeData = event.getData().getData();
                                LOG.info("child node data: {}", nodeData == null? "" : new String(nodeData));
                            }
                            //释放锁
                            if(canReleaseLock) {
                                zkClient.delete().forPath(path + "/lock");
                            }

                            break;
                        case CHILD_REMOVED:
                            LOG.info("child removed, node path: {}", event.getData().getPath());
                            break;
                    }
                });
            });
        }
    }

}
