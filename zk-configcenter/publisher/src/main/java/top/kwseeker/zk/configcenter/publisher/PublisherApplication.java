package top.kwseeker.zk.configcenter.publisher;

//import org.I0Itec.zkclient.ZkClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.kwseeker.zk.configcenter.publisher.config.ZkConfigCenterProperties;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 设置配置数据
 */
@SpringBootApplication
public class PublisherApplication implements ApplicationRunner {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private CuratorFramework zkClient;
    @Autowired
    private ZkConfigCenterProperties properties;

    public static void main(String[] args) {
        SpringApplication.run(PublisherApplication.class, args);
    }

    /**
     * 创建配置数据znode
     * 订阅者服务的配置节点
     * /zk-template/zk-configcenter/subscriber-a/application.properties
     * /zk-template/zk-configcenter/subscriber-b/application.properties
     * 全局配置节点
     * /zk-template/zk-configcenter/global.properties
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        CuratorFrameworkState state = zkClient.getState();
        if(CuratorFrameworkState.LATENT == state) {
            zkClient.start();
        }

        log.debug("创建配置的znode:");
        String znodeBase = properties.getZnodeBase();
        Map<String, List<String>> subscriberPropFiles = properties.getSubscriberPropFiles();
        Set<Map.Entry<String, List<String>>> entries = subscriberPropFiles.entrySet();
        for (Map.Entry<String, List<String>> entry : entries) {
            String subscriberName = entry.getKey();
            List<String> propFiles = entry.getValue();
            for(String propFile : propFiles) {
                String[] result = propFile.split("resources");
                String znodePath = znodeBase + "/" + subscriberName + result[result.length-1];
                createPersistentIfNotExist(znodePath, readFile(propFile));
            }
        }
        String globalPropFilePath = znodeBase + properties.getGlobalPropFile();
        createPersistentIfNotExist(globalPropFilePath, null);
    }

    public void createPersistentIfNotExist(String path, byte[] bytes) throws Exception {
        if(zkClient.checkExists().forPath(path) == null) {
            log.debug("---> " + path);
            zkClient.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(path, bytes);
        } else {
            log.debug("---> " + path + " already exist!");
            zkClient.setData().forPath(path, bytes);
        }
    }

    private byte[] readFile(String path) throws IOException {
        FileInputStream fis = null;
        byte[] content = null;
        try {
            fis = new FileInputStream(path);
            content = new byte[fis.available()];
            int readCount = fis.read(content);
            if(readCount != fis.available()) {
                log.warn("read count not equal to available");
            }
        } finally {
            if(fis != null) {
                fis.close();
            }
        }
        return content;
    }
}
