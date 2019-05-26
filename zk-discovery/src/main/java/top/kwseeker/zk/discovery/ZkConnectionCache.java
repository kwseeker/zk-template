package top.kwseeker.zk.discovery;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.zk.discovery.util.PropertyUtil;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 缓存服务注册和发现过程中的实例
 */
public class ZkConnectionCache {

    private static final Logger LOG = LoggerFactory.getLogger(ZkConnectionCache.class);

    //以IP和端口(如：127.0.0.1:2181)为键的连接map
    public static Map<String, DiscoveryClient> zkClientMap = Maps.newConcurrentMap();
    //zk服务器ip port
    public static Set<String> serverSet = Sets.newHashSet();

    static {
        try {
            Properties props = PropertyUtil.load("zookeeper.properties");
            Set<String> keys = props.stringPropertyNames();
            String pattern = "zk\\.servers\\[[0-9]+\\]";    // zk.servers[[0-9]+]
            for(String key : keys) {
                if(Pattern.matches(pattern, key)) {
                    serverSet.add(props.getProperty(key));
                }
            }
            for(String key : serverSet) {
                CuratorFramework client = CuratorFrameworkFactory.builder()
                        .connectString(key)
                        .sessionTimeoutMs(Integer.valueOf(props.getProperty("zk.server.timeout")))
                        .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                        .build();
                zkClientMap.put(key, new DiscoveryClient(key, client));
                client.start();
            }
        } catch (IOException e) {
            LOG.error("zk client initial error, e=" + e.getMessage());
            e.printStackTrace();
        }
    }

    public DiscoveryClient getZkClient(String ip, String port) {
        String serverUri = ip + ":" + port;
        return getZkClient(serverUri);
    }

    public DiscoveryClient getZkClient(String serverUri) {
        return zkClientMap.get(serverUri);
    }

    //Client针对不同的服务有不同的ServiceDiscovery和ServiceProvider缓存
    public static class DiscoveryClient {
        String serverUri;
        CuratorFramework client;
        //ServiceProvider 服务发现的策略对象
        Map<String, ServiceProvider<ServiceDetail>> serviceProviderMap;
        //ServiceDiscovery 根据服务详情信息（ServiceDetail.class）, zk客户端, 服务根路径，创建的用于获取服务实例的对象
        Map<String, ServiceDiscovery<ServiceDetail>> serviceDiscoveryMap;

        DiscoveryClient(String serverUri, CuratorFramework client) {
            this.serverUri = serverUri;
            this.client = client;
            this.serviceProviderMap = Maps.newConcurrentMap();
            this.serviceDiscoveryMap = Maps.newConcurrentMap();
        }
    }
}
