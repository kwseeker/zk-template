package top.kwseeker.zkboot.zkbootdiscovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.kwseeker.zkboot.zkbootdiscovery.manager.ZKManager;

/**
 * 后面将各个模块的模块实现细节均放到主类里面
 *
 * zookeeper 服务配置
 * zookeeper client 配置
 *
 * 创建连接缓存池，zk-boot-manager中针对每个zookeeper节点创建一个连接
 *
 * 注册监听服务，当有服务通过连接注册新的服务到ZK时，发布消息通知所有微服务更新本地服务缓存列表
 *
 */
@SpringBootApplication
public class ZkBootDiscoveryApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ZkBootDiscoveryApplication.class);

    @Autowired
    private ZKManager zkManager;

    public static void main(String[] args) {
        SpringApplication.run(ZkBootDiscoveryApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("zkManager 初始化");
        zkManager.init();
    }
}
