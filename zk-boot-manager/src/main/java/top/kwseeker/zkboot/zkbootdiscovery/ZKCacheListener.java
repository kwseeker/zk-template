package top.kwseeker.zkboot.zkbootdiscovery;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.zkboot.service.ServiceListener;
import top.kwseeker.zkboot.service.ServiceNode;

public class ZKCacheListener implements TreeCacheListener {

    private static final Logger LOG = LoggerFactory.getLogger(ZKCacheListener.class);

    private final String watchPath;
    private final ServiceListener listener;
    private Gson gson = new Gson();

    public ZKCacheListener(String watchPath, ServiceListener listener) {
        this.watchPath = watchPath;
        this.listener = listener;
    }

    @Override
    public void childEvent(CuratorFramework curator, TreeCacheEvent event) throws Exception {
        ChildData data = event.getData();
        if (data == null) return;
        String dataPath = data.getPath();
        if (Strings.isNullOrEmpty(dataPath)) return;
        if (dataPath.startsWith(watchPath)) {
            switch (event.getType()) {
                case NODE_ADDED:
                    listener.onServiceAdded(dataPath, gson.fromJson(new String(data.getData()), ServiceNode.class));
                    break;
                case NODE_REMOVED:
                    listener.onServiceRemoved(dataPath, gson.fromJson(new String(data.getData()), ServiceNode.class));
                    break;
                case NODE_UPDATED:
                    listener.onServiceUpdated(dataPath, gson.fromJson(new String(data.getData()), ServiceNode.class));
                    break;
            }
            LOG.info("ZK node data change={}, nodePath={}, watchPath={}, ns={}");
        }
    }
}
