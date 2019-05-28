package top.kwseeker.zkboot.zkbootdiscovery.manager;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kwseeker.zkboot.zkbootdiscovery.config.ZKConfig;
import top.kwseeker.zkboot.zkbootdiscovery.util.ZKConnectionCache;

import java.util.Map;
import java.util.Set;

@Service
public class ZKManager {

    private static final Logger LOG = LoggerFactory.getLogger(ZKManager.class);

    @Autowired
    private ZKConfig zkConfig;
    @Autowired
    private ZKConnectionCache cache;

    //初始化: 创建连接缓存池，创建根节点
    public void init() {
        //创建连接缓存池
        cache.initialCache();

        //获取连接并创建持久根节点
        CuratorFramework zkConnection = cache.randomGetConnection();
        String basePath = zkConfig.getBasePath();
        try {
            Stat stat = zkConnection.checkExists().forPath(basePath);
            if(stat == null) {
                zkConnection.create()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(basePath);
            }
        } catch (Exception e) {
            LOG.error("check or create base path failed, e={}", e.getMessage());
            e.printStackTrace();
        }

        //注册监听服务，用于发布服务列表变更消息，通知微服务过来从zookeeper上获取最新的服务列表
        //感觉比较好的服务发现的实现方案应该是zkManager主动通知微服务服务节点变更
        //微服务节点请求做的事：注册服务，除非本地服务列表无法找到服务才主动去zookeeper请求更新服务列表
        //zkManager: 监听微服务节点变更（服务注册、服务注销、服务更新，同一个微服务的不同服务实例使用SEQUENTIAL类型节点）
        Set<Map.Entry<String, CuratorFramework>> entrySet = cache.getZkConnMap().entrySet();
        for(Map.Entry<String, CuratorFramework> entry : entrySet) {
            //每个client都添加监听
            PathChildrenCache pathChildrenCache = new PathChildrenCache(entry.getValue(), zkConfig.getBasePath(), true);
            pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                    LOG.debug("child node change event happened");
                    //TODO：根据具体事件做相应的处理

                }
            });

        }
    }
}
