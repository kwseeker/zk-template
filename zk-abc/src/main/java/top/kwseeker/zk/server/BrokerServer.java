package top.kwseeker.zk.server;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class BrokerServer {

    private static final Logger LOG = LoggerFactory.getLogger(BrokerServer.class);

    private static final String ROOT = "/local";
    private static final String DATA = "123";

    public static void main(String[] args) throws Exception {
        //创建一个zk客户端
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")    //连接本地2181端口
                .sessionTimeoutMs(5000)             //会话超时时间
                .connectionTimeoutMs(3000)          //连接超时时间
                .retryPolicy(new ExponentialBackoffRetry(1000,3))   //
                .build();
        //客户端启动
        zkClient.start();
        //程序退出后关闭客户端
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            zkClient.close();
        }));

        TimeUnit.SECONDS.sleep(5);

        //先判断根节点是否存在不存在创建根节点
        Stat stat = zkClient.checkExists().forPath(ROOT);
        if(stat == null) {
            zkClient.create().withMode(CreateMode.PERSISTENT).forPath(ROOT);
        }
        //判断子节点是否存在不存在则创建
        String[] paths = {ROOT + "/task1", ROOT + "/task2"};
        for (String path : paths) {
            if(null != zkClient.checkExists().forPath(path)) {
                LOG.error("node already exist");
                return;
            }
            zkClient.create().withMode(CreateMode.PERSISTENT).forPath(path, DATA.getBytes());
        }

        TimeUnit.SECONDS.sleep(3000);
    }
}
