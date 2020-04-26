package top.kwseeker.zkboot.zkbootdiscovery;

import com.google.gson.Gson;
import top.kwseeker.zkboot.service.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.curator.utils.ZKPaths.PATH_SEPARATOR;

public class ZKServiceRegistryAndDiscovery extends BaseService implements ServiceRegistry, ServiceDiscovery {

    private static final ZKServiceRegistryAndDiscovery INSTANCE = new ZKServiceRegistryAndDiscovery();
    //Zookeeper客户端连接
    private final ZKClient client;
    private Gson gson = new Gson();

    public ZKServiceRegistryAndDiscovery() {
        this.client = ZKClient.INSTANCE;
    }

    public static ZKServiceRegistryAndDiscovery getInstance() {
        return INSTANCE;
    }

    @Override
    public void start(Listener listener) {
        if (isRunning()) {
            listener.onSuccess();
        } else {
            super.start(listener);
        }
    }

    @Override
    public void stop(Listener listener) {
        if (isRunning()) {
            super.stop(listener);
        } else {
            listener.onSuccess();
        }
    }

    @Override
    protected void doStart(Listener listener) throws Throwable {
        client.start(listener);
    }

    @Override
    protected void doStop(Listener listener) throws Throwable {
        client.stop(listener);
    }

    @Override
    public void register(ServiceNode node) {
        if (node.isPersistent()) {
            client.registerPersist(node.nodePath(), gson.toJson(node));
        } else {
            client.registerEphemeral(node.nodePath(), gson.toJson(node));
        }
    }

    @Override
    public void unregister(ServiceNode node) {
        if (client.isRunning()) {
            client.remove(node.nodePath());
        }
    }

    @Override
    public List<ServiceNode> lookup(String serviceName) {
        List<String> childrenKeys = client.getChildrenKeys(serviceName);
        if (childrenKeys == null || childrenKeys.isEmpty()) {
            return Collections.emptyList();
        }

        return childrenKeys.stream()
                .map(key -> serviceName + PATH_SEPARATOR + key)
                .map(client::get)
                .filter(Objects::nonNull)
                .map(childData -> gson.fromJson(childData, ServiceNode.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void subscribe(String watchPath, ServiceListener listener) {
        client.registerListener(new ZKCacheListener(watchPath, listener));
    }

    @Override
    public void unsubscribe(String path, ServiceListener listener) {

    }
}
