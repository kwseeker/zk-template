package top.kwseeker.zk.discovery;

import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MicroServiceConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(MicroServiceConsumer.class);

    public static void main(String[] args) throws Exception {
        ServiceDiscoverer discoverer = new ServiceDiscoverer();
        ServiceInstance<ServiceDetail> instance;
        for (int i = 0; i < 20; i++) {
            instance = discoverer.randomGetServiceInstance("127.0.0.1:2181", "greetService");
            LOG.info("serviceName: {}, serviceId: {}", instance.getName(), instance.getId());
        }
    }
}
