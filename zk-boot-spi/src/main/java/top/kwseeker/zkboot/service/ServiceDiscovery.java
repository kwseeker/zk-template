package top.kwseeker.zkboot.service;

import java.util.List;

/**
 * 服务发现接口
 */
public interface ServiceDiscovery {

    List<ServiceNode> lookup(String path);

    void subscribe(String path, ServiceListener listener);

    void unsubscribe(String path, ServiceListener listener);
}
