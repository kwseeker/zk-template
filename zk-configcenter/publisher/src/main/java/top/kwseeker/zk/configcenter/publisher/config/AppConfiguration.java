package top.kwseeker.zk.configcenter.publisher.config;

//import org.I0Itec.zkclient.ZkClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Value("${zk.cluster.addr}")
    private String zkCluster;

    @Bean
    public CuratorFramework zkClient() {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        return CuratorFrameworkFactory.builder()
                .connectString(zkCluster)
                .sessionTimeoutMs(15000)
                .connectionTimeoutMs(10000)
                .retryPolicy(retryPolicy)
                .namespace("config-center")     //客户端隔离命名空间；会作为根节点被创建
                .build();
    }

    //@Bean
    //public ZkClient zkClient() {
    //    return new ZkClient(zkCluster);
    //}
}
