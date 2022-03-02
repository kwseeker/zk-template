package top.kwseeker.zk.abc.official;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class StandaloneBase {

    private static final String SERVERS = "localhost:2181";
    private static final int SESSION_TIMEOUT_MS = 60 * 1000;
    private static final int CONNECTION_TIMEOUT_MS = 5000;

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private static ZooKeeper zookeeper = null;

    @Before
    public void init(){
        try {
            log.info("Start to connect to zookeeper server: {}", getServers());

            //ZooKeeper中创建了两个守护线程（异步执行）（sendThread eventThread）
            //sendThread 通信
            //eventThread 处理时间监听回调
            zookeeper = new ZooKeeper(getServers(), getSessionTimeoutMs(), new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getState() == Event.KeeperState.SyncConnected
                            && watchedEvent.getType() == Event.EventType.None) {
                        countDownLatch.countDown();
                        log.info("Connect succeed!");
                    }
                }
            });

            log.info(" Connecting ...");
            countDownLatch.await();     //等待连接建立完成
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void release() {
        try {
            TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static ZooKeeper getZookeeper() {
        return zookeeper;
    }

    protected String getServers() {
        return SERVERS;
    }

    protected int getSessionTimeoutMs() {
        return SESSION_TIMEOUT_MS;
    }
}
