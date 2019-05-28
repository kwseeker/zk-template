package top.kwseeker.zkboot.zkbootdiscovery.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import top.kwseeker.zkboot.zkbootdiscovery.config.ZKConfig;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * zookeeper连接缓冲池
 */
public class ZKConnectionCache {

    private AtomicInteger counter = new AtomicInteger(1);

    @Autowired
    private ZKConfig zkConfig;

    //暂时为每个zk节点创建一个连接，TODO：优化
    private Map<String, CuratorFramework> zkConnMap = Maps.newConcurrentMap();

    public void initialCache() {
        List<String> addrs = zkConfig.getAddrs();
        Set<String> addrSet = Sets.newHashSet();
        addrSet.addAll(addrs);
        for(String addr: addrSet) {
            CuratorFramework connection = CuratorFrameworkFactory.builder()
                    .connectString(addr)
                    .connectionTimeoutMs(zkConfig.getConnectionTimeout())
                    .retryPolicy(new ExponentialBackoffRetry(zkConfig.getMinSleepTimeMs(),
                            zkConfig.getMaxRetry(),
                            zkConfig.getMaxSleepTimeMs()))
                    .build();
            zkConnMap.put(addr, connection);
        }
    }

    public CuratorFramework randomGetConnection() {
        Object[] keys = zkConnMap.keySet().toArray();
        String randomKey = (String) keys[new Random().nextInt(keys.length)];
        return zkConnMap.get(randomKey);
    }

    public Map<String, CuratorFramework> getZkConnMap() {
        return zkConnMap;
    }

    public void setZkConnMap(Map<String, CuratorFramework> zkConnMap) {
        this.zkConnMap = zkConnMap;
    }
}
