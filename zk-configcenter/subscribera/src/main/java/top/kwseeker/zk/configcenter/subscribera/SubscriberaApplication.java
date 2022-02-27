package top.kwseeker.zk.configcenter.subscribera;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.kwseeker.zk.configcenter.subscribera.config.ZkConfigNodes;

import java.io.FileOutputStream;
import java.io.IOException;

@SpringBootApplication
public class SubscriberaApplication implements ApplicationRunner {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private CuratorFramework zkClient;
    @Autowired
    private ZkConfigNodes zkConfigNodes;

    public static void main(String[] args) {
        SpringApplication.run(SubscriberaApplication.class, args);
    }

    /**
     * 添加watcher监控本微服务对应的配置文件的变化，发生变化后进行配置更新
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(CuratorFrameworkState.LATENT == zkClient.getState()) {
            zkClient.start();
        }
        //创建watcher监听处理配置变化
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                log.debug("watcher event occur: " + watchedEvent);
                if(watchedEvent.getType() == Event.EventType.NodeDataChanged) {
                    //读取数据内容，刷新到本地
                    FileOutputStream fos = null;
                    try {
                        String path = watchedEvent.getPath();
                        String[] array = path.split("/");
                        String propFileName = array[array.length-1];
                        byte[] data = zkClient.getData().forPath(path);
                        String propFilePath = Thread.currentThread().getContextClassLoader().getResource(propFileName).toURI().getPath();
                        fos = new FileOutputStream(propFilePath);
                        fos.write(data);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        e.printStackTrace();
                    } finally {
                        if(fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        };
        for (String path : zkConfigNodes.getConfigNodes()) {
            zkClient.getData().usingWatcher(watcher).forPath(path);
        }
    }
}
