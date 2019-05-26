package top.kwseeker.zk.discovery;

import org.apache.curator.x.discovery.ServiceInstance;

import java.io.IOException;
import java.util.Collection;

public interface DiscoverService {

    Collection<ServiceInstance<ServiceDetail>> getAllServiceInstance(String serverUri, String serviceName) throws Exception;

    ServiceInstance<ServiceDetail> randomGetServiceInstance(String serverUri, String serviceName) throws Exception;

    void close() throws IOException;
}
