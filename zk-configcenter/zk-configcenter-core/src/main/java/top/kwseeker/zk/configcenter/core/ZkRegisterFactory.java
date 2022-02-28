package top.kwseeker.zk.configcenter.core;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class ZkRegisterFactory {

    private static final Map<String, ZkRegister> cache = Collections.synchronizedMap(new WeakHashMap<>(5));

    /**
     * 获取配置中心
     * @param servers zk服务器节点集合
     * @return
     */
    public static synchronized ZkRegister getZkRegister(String servers) {
        if(!cache.containsKey(servers)) {
            ZkRegister register = new ZkRegister(servers);
            cache.put(servers,register);
        }
        return cache.get(servers);
    }
}
