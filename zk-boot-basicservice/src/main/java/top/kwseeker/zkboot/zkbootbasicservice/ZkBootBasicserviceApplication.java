package top.kwseeker.zkboot.zkbootbasicservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.kwseeker.zkboot.zkbootbasicservice.manager.BasicServer;
import top.kwseeker.zkboot.zkbootdiscovery.ZKServiceRegistryAndDiscovery;

/**
 * 服务提供者
 *
 *
 */
@SpringBootApplication
public class ZkBootBasicserviceApplication implements CommandLineRunner {

    @Autowired
    private BasicServer basicServer;

    private static final Logger LOG = LoggerFactory.getLogger(ZkBootBasicserviceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ZkBootBasicserviceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //new Thread(() -> {
        //    try {
        //        LOG.info("启动GRPC服务端");
        //        final BasicServer server = new BasicServer();
        //        server.start();
        //        server.blockUntilShutdown();
        //    } catch (IOException e) {
        //        e.printStackTrace();
        //    } catch (InterruptedException e) {
        //        e.printStackTrace();
        //    }
        //}).start();
        basicServer.init();
        LOG.info("启动GRPC服务");
        basicServer.start();
        LOG.info("注册GRPC服务节点");
        ZKServiceRegistryAndDiscovery zkSrd = ZKServiceRegistryAndDiscovery.getInstance();
        zkSrd.syncStart();  //启动ZK客户端连接
        zkSrd.register(basicServer.getZkServiceNode());
        basicServer.blockUntilShutdown();
    }
}
