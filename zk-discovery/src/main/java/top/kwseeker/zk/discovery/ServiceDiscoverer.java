package top.kwseeker.zk.discovery;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.strategies.RandomStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.zk.discovery.util.PropertyUtil;

import java.io.IOException;
import java.util.Collection;

/**
 * 服务发现
 */
public class ServiceDiscoverer implements DiscoverService {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceDiscovery.class);

    private static final JsonInstanceSerializer<ServiceDetail> serializer = new JsonInstanceSerializer<>(ServiceDetail.class);
    private String basePath;

    public ServiceDiscoverer() {
        try {
            basePath = PropertyUtil.load("zookeeper.properties").getProperty("zk.service.basepath");
        } catch (IOException e) {
            LOG.error("read zk.service.basepath failed, e=" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Collection<ServiceInstance<ServiceDetail>> getAllServiceInstance(String serverUri, String serviceName) throws Exception {
        ServiceProvider<ServiceDetail> provider = getDiscoveryProvider(serverUri, serviceName);
        if(provider == null) {
            LOG.error("failed get ServiceProvider");
            return null;
        }
        Collection<ServiceInstance<ServiceDetail>> instances = provider.getAllInstances();
        if(instances == null) {
            LOG.error("No instance named: {} be found", serviceName);
        }
        //provider.close();
        return instances;
    }

    //通过服务名随机获取一个服务实例
    @Override
    public ServiceInstance<ServiceDetail> randomGetServiceInstance(String serverUri, String serviceName) throws Exception {
        ServiceProvider<ServiceDetail> provider = getDiscoveryProvider(serverUri, serviceName);
        if(provider == null) {
            LOG.error("failed get ServiceProvider");
            return null;
        }
        ServiceInstance<ServiceDetail> instance = provider.getInstance();
        if(instance == null) {
            LOG.error("The instance named: {} be found", serviceName);
        }
        //provider.close();
        return instance;
    }

    private ServiceProvider<ServiceDetail> getDiscoveryProvider(String serverUri, String serviceName) throws Exception {
        //先从缓存中取ServiceProvider，取不到再通过ServiceDiscovery创建
        ZkConnectionCache.DiscoveryClient client = ZkConnectionCache.zkClientMap.get(serverUri);
        if(client == null) {
            LOG.error("wrong serverUri");
            return null;
        }
        ServiceProvider<ServiceDetail> provider = client.serviceProviderMap.get(serviceName);
        if(provider == null) {
            ServiceDiscovery<ServiceDetail> serviceDiscovery = client.serviceDiscoveryMap.get(serviceName);
            if(serviceDiscovery == null) {
                serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceDetail.class)
                        .client(client.client)
                        .basePath(basePath)
                        .serializer(serializer)
                        .build();
                client.serviceDiscoveryMap.put(serviceName, serviceDiscovery);
                serviceDiscovery.start();
            }
            provider = serviceDiscovery.serviceProviderBuilder()
                    .serviceName(serviceName)
                    .providerStrategy(new RandomStrategy<>())
                    .build();
            client.serviceProviderMap.put(serviceName, provider);
            provider.start();
        }
        return provider;
    }

    @Override
    public void close() throws IOException {
    }
}
