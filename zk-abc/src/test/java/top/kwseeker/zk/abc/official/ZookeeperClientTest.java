package top.kwseeker.zk.abc.official;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;
import top.kwseeker.zk.abc.Constants;

import java.io.IOException;

@Slf4j
public class ZookeeperClientTest extends StandaloneBase {

    private static final String PATH = Constants.ROOT_PATH + ZookeeperClientTest.class.getName();

    @Test
    public void testBaseOperations() throws KeeperException, InterruptedException, IOException {
        ZooKeeper zooKeeper = getZookeeper();
        //写入
        ConfigItem item = new ConfigItem();
        item.setKey("env");
        item.setValue("test");
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes = objectMapper.writeValueAsBytes(item);
        Stat nodeStat = zooKeeper.exists(PATH, false);
        if (nodeStat == null) {
            String s = getZookeeper().create(PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            log.debug("Node data created, path:{}", s);
        }

        Watcher watcher = new Watcher() {
            @SneakyThrows
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getType() == Event.EventType.NodeDataChanged
                        && watchedEvent.getPath() != null
                        && watchedEvent.getPath().equals(PATH)) {
                    log.debug("Node data changed: {}", watchedEvent.getPath());

                    byte[] newData = getZookeeper().getData(PATH, this, null);      //处理事件时重新注册一次，实现长期监听
                    ConfigItem item = objectMapper.readValue(new String(newData), ConfigItem.class);
                    log.debug("New data: {}", item);
                }
            }
        };
        Stat rawStat = new Stat();
        byte[] data = getZookeeper().getData(PATH, watcher, rawStat);      //处理事件时重新注册一次，实现长期监听
        ConfigItem rawItem = objectMapper.readValue(new String(data), ConfigItem.class);
        log.debug(">>>>>>> raw data: {}, stat: {}", rawItem, rawStat.toString());

        rawItem.setValue("prod");
        Stat newStat = getZookeeper().setData(PATH, objectMapper.writeValueAsBytes(rawItem), rawStat.getVersion());
        log.debug(">>>>>>> new stat: {}", newStat.toString());
    }
}
