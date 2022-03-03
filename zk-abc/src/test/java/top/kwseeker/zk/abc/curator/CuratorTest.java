package top.kwseeker.zk.abc.curator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

@Slf4j
public class CuratorTest {

    private static final String SERVERS = "localhost:2181";
    private static final int SESSION_TIMEOUT_MS = 60 * 1000;
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

    @After
    public void release() {
        try {
            TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBaseOperations() throws Exception {
        String path = ROOT_PATH;
        //节点不存在时查询会报错
        //byte[] bytes = curatorFramework.getData().forPath(path);
        //log.info(">>>>>>> get node data, root node: {}, value: {}", path, new String(bytes));

        createIfNeed(path);

        byte[] bytes = curatorFramework.getData().forPath(path);
        log.info(">>>>>>> get node data, root node: {}, value: {}", path, new String(bytes));

        //注册监听（长期监听）
        curatorFramework.getCuratorListenable().addListener((CuratorFramework client, CuratorEvent event) -> {
            log.info(">>>>>>> node data changed: event: {},  {} ", event.getType().name(), event);
        });
        //一次性监听
        byte[] bytesWatched = curatorFramework.getData().usingWatcher(new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getType() == Event.EventType.NodeDataChanged) {
                    log.info(">>>>>>> node {} data changed!", event.getPath());
                }
            }
        }).forPath(path);

        //写/更新数据
        log.debug(">>>>>>> set operation 1");
        curatorFramework.setData().forPath(path, "test set".getBytes());
        log.debug(">>>>>>> set operation 2");
        curatorFramework.setData().forPath(path, "test set2".getBytes());
        //
        log.debug(">>>>>>> set background operation 1");
        curatorFramework.setData().inBackground().forPath(path, "test background set".getBytes());
    }

    //https://curator.apache.org/curator-recipes/curator-cache.html
    //这种写法依赖 recipes:5.0.0 zookeeper:3.6.0 及以上版本， 这里测试用的 zookeeper:3.5.8所以不支持这种写法
    //@Test
    //public void testCuratorCacheListen() {
    //    CuratorCache curatorCache = CuratorCache.builder(curatorFramework, ROOT_PATH).build();
    //    CuratorCacheListener listener = CuratorCacheListener.builder()
    //            .forNodeCache(new NodeCacheListener() {
    //                @Override
    //                public void nodeChanged() throws Exception {
    //                    log.info("node: data changed, new data=");
    //                    //log.info("node:{} data changed, new data={}", curatorCache.get(), curatorCache.getCurrentData().getData());
    //                }
    //            }).build();
    //    curatorCache.listenable().addListener(listener);
    //    curatorCache.start();
    //}

    @Test
    public void testNodeCacheListen() throws Exception {
        NodeCache nodeCache = new NodeCache(curatorFramework, ROOT_PATH);
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                String value = new String(nodeCache.getCurrentData().getData());
                log.info("node:{} data changed, new data={}", nodeCache.getPath(), value);
            }
        });
        nodeCache.start(true);
    }

    public void createIfNeed(String path) throws Exception {
        Stat stat = curatorFramework.checkExists().forPath(path);
        if (stat == null) {
            String s = curatorFramework.create().creatingParentsIfNeeded().forPath(path);
            log.info("path {} created! ", s);
        }
    }
}
