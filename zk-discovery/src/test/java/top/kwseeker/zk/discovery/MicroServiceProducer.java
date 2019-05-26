package top.kwseeker.zk.discovery;

import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class MicroServiceProducer {

    private static final Logger LOG = LoggerFactory.getLogger(MicroServiceProducer.class);

    public static void main(String[] args) {
        try {
            ServiceRegistrar registrar = new ServiceRegistrar();
            registrar.registerService("127.0.0.1:2181", "greetService", "1",
                    "localhost", 8080, new ServiceDetail("test service 1"));
            registrar.registerService("127.0.0.1:2181", "greetService", "2",
                    "localhost", 8081, new ServiceDetail("test service 2"));

            TimeUnit.SECONDS.sleep(5);

            ServiceDiscoverer discoverer = new ServiceDiscoverer();
            Collection<ServiceInstance<ServiceDetail>> instances = discoverer.getAllServiceInstance("127.0.0.1:2181", "greetService");
            if(instances != null && instances.size() > 0) {
                LOG.info("ServiceInstance node register success");
            } else {
                LOG.error("ServiceInstance node register failed");
            }

            //由于创建的是动态服务实例，会话结束会自动删除ServiceInstance，为了调试延时一下
            TimeUnit.SECONDS.sleep(1000);
        } catch (Exception e) {
            LOG.error("ServiceInstance node register failed, e=" + e.getMessage());
            e.printStackTrace();
        }
    }
}
