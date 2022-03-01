package top.kwseeker.zk.abc.curator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class CuratorTest {

    private static final String SERVERS = "localhost:2181";
    private static final int SESSION_TIMEOUT_MS = 60*1000;
    private static final int CONNECTION_TIMEOUT_MS = 5000;
    private static final String ROOT_PATH = "/abc/" + CuratorTest.class.getName();

    private CuratorFramework curatorFramework;

    @Before
    public void init() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(5000, 30);
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(SERVERS)
                .retryPolicy(retryPolicy)
                .sessionTimeoutMs(SESSION_TIMEOUT_MS)
                .connectionTimeoutMs(CONNECTION_TIMEOUT_MS)
                .canBeReadOnly(true)
                .build();
        curatorFramework.getConnectionStateListenable().addListener((client, newState) -> {
            if (newState == ConnectionState.CONNECTED) {
                log.info("connect succeed！");
            }
        });
        log.info("connecting ...");
        curatorFramework.start();
    }

    @Test
    public void testBaseOperations() throws Exception {
        byte[] bytes = curatorFramework.getData().forPath(ROOT_PATH);
        log.info("get node data, root node: {}, value: {}", ROOT_PATH, new String(bytes));


    }


}
