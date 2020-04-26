package top.kwseeker.zkboot.service;

/**
 * 服务注册接口
 *
 * 微服务注册，注册哪些内容
 */
public interface ServiceRegistry {

    void register(ServiceNode node);
    void unregister(ServiceNode node);
}
