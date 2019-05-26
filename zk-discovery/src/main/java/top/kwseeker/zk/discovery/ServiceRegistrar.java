package top.kwseeker.zk.discovery;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.zk.discovery.util.PropertyUtil;

import java.io.IOException;
import java.util.Collection;

public class ServiceRegistrar implements RegisterService {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceRegistrar.class);

    private static final JsonInstanceSerializer<ServiceDetail> serializer = new JsonInstanceSerializer<>(ServiceDetail.class);
    private String basePath;

    public ServiceRegistrar() {
        try {
            basePath = PropertyUtil.load("zookeeper.properties").getProperty("zk.service.basepath");
        } catch (IOException e) {
            LOG.error("read zk.service.basepath failed, e=" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void registerService(String serverUri,
                                ServiceInstance<ServiceDetail> instance) throws Exception {
        ZkConnectionCache.DiscoveryClient client = ZkConnectionCache.zkClientMap.get(serverUri);
        if(client == null) {
            LOG.error("zkClient should not be null");
            return;
        }
        registerService(client, instance, basePath);
    }

    @Override
    public void registerService(String serverUri,
                                String serviceName, String serviceId, String ip, int port, ServiceDetail payload) throws Exception {
        ServiceInstance<ServiceDetail> instance = ServiceInstance.<ServiceDetail>builder()
                .name(serviceName)
                .id(serviceId)
                .address(ip)
                .port(port)
                .payload(payload)
                .registrationTimeUTC(System.currentTimeMillis())
                .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                .build();
        registerService(serverUri, instance);
    }

    private void registerService(ZkConnectionCache.DiscoveryClient client,
                                ServiceInstance<ServiceDetail> instance,
                                String basePath) throws Exception {
        String serviceName = instance.getName();
        ServiceDiscovery<ServiceDetail> serviceDiscovery = client.serviceDiscoveryMap.get(serviceName);
        if(serviceDiscovery == null) {
            serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceDetail.class)
                    .client(client.client)
                    .basePath(basePath)             //节点存储到zookeeper中的根路径
                    .serializer(serializer)
                    .build();
            client.serviceDiscoveryMap.put(serviceName, serviceDiscovery);
            serviceDiscovery.start();
        }
        //TODO: 判断 ServiceDiscovery 是否已经开启了
        //如果这个instance对应的id号已经存在，则会覆盖之前注册的ServiceInstance
        serviceDiscovery.registerService(instance);
    }

    @Override
    public void unregisterService(String serverUri, String serviceName) throws Exception {
        ZkConnectionCache.DiscoveryClient client = ZkConnectionCache.zkClientMap.get(serverUri);
        ServiceDiscovery<ServiceDetail> serviceDiscovery = client.serviceDiscoveryMap.get(serviceName);
        if(serviceDiscovery == null) {
            serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceDetail.class)
                    .client(client.client)
                    .basePath(basePath)             //节点存储到zookeeper中的根路径
                    .serializer(serializer)
                    .build();
            client.serviceDiscoveryMap.put(serviceName, serviceDiscovery);
            serviceDiscovery.start();
        }
        Collection<ServiceInstance<ServiceDetail>> services = serviceDiscovery.queryForInstances(serviceName);
        for (ServiceInstance<ServiceDetail> instance : services) {
            serviceDiscovery.unregisterService(instance);
        }
        closeServiceDiscovery(serviceDiscovery);
        client.serviceDiscoveryMap.remove(serviceName);
    }

    @Override
    public void unregisterServiceInstance(String serverUri, String serviceName, String instanceId) throws Exception {
        ZkConnectionCache.DiscoveryClient client = ZkConnectionCache.zkClientMap.get(serverUri);
        ServiceDiscovery<ServiceDetail> serviceDiscovery = client.serviceDiscoveryMap.get(serviceName);
        if(serviceDiscovery == null) {
            LOG.warn("serviceDiscovery for this service instance not exist");
            return;
        }
        ServiceInstance<ServiceDetail> instance = serviceDiscovery.queryForInstance(serviceName, instanceId);
        serviceDiscovery.unregisterService(instance);
        Collection<ServiceInstance<ServiceDetail>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
        if(serviceInstances == null || serviceInstances.isEmpty()) {
            closeServiceDiscovery(serviceDiscovery);
            client.serviceDiscoveryMap.remove(serviceName);
        }
    }

    private void closeServiceDiscovery(ServiceDiscovery<ServiceDetail> serviceDiscovery) throws Exception {
        serviceDiscovery.close();
    }
}
