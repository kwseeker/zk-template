package top.kwseeker.zk.discovery;

import org.apache.curator.x.discovery.ServiceInstance;

/**
 * 服务注册、更新、注销
 */
public interface RegisterService {

    /**
     * 服务注册
     * @param serverUri 连接到的zookeeper服务端
     * @param instance  服务实例
     * @throws Exception
     */
    void registerService(String serverUri,
                         ServiceInstance<ServiceDetail> instance) throws Exception;

    /**
     * 服务注册
     * @param client    连接到zookeeper服务端的连接
     * @param serviceName   服务名称，所有服务实例公用
     * @param serviceId     服务实例Id，每个服务的每个实例有唯一id
     * @param ip            服务实例ip
     * @param port          服务实例端口号
     * @param payload   服务实体
     * @throws Exception
     */
    void registerService(String serverUri,
                         String serviceName, String serviceId, String ip, int port, ServiceDetail payload) throws Exception;

    /**
     * 注销服务（包括所有实例）
     * @param serviceName   服务名
     * @throws Exception
     */
    void unregisterService(String serverUri, String serviceName) throws Exception;

    /**
     * 注销服务实例
     * @param serviceName   服务名称
     * @param instanceId        服务实例id
     * @throws Exception
     */
    void unregisterServiceInstance(String serverUri, String serviceName, String instanceId) throws Exception;

    //ServiceDiscovery.registerService()本身就可以覆盖旧的实例
    //void updateService();

    //void close();
}
