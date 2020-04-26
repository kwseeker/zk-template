package top.kwseeker.zkboot.zkbootbasicservice.manager;


import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.kwseeker.zkboot.service.ServiceNode;
import top.kwseeker.zkboot.zkbootbasicservice.service.GreetServiceImpl;

import java.io.IOException;

/**
 *
 */
@Component
public class BasicServer {

    private static final Logger LOG = LoggerFactory.getLogger(BasicServer.class);

    @Value("${zknode.ip}")
    private String ip = "127.0.0.1";
    @Value("${zknode.port}")
    private int port = 9011;
    @Value("${zknode.name}")
    private String serviceName;
    @Value("${zknode.persistent}")
    private boolean persistent;
    //GRPC服务端
    private Server server;
    //Zookeeper节点
    private ServiceNode zkServiceNode;

    public void init() {
        zkServiceNode = new ServiceNode(ip, port, serviceName, persistent);
    }

    /**
     * GRPC服务启动
     * @throws IOException
     */
    public void start() throws IOException {
        //默认是使用netty实现的服务端
        server = ServerBuilder.forPort(port)
                .addService(new GreetServiceImpl())
                .build()
                .start();
        LOG.info("GRPC Server started, listening on "+ port);

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                BasicServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    /**
     * 注册服务实例到zookeeper
     */
    public void registerServer() {

    }

    /**
     * GRPC服务关闭
     */
    public void stop(){
        if (server != null){
            server.shutdown();
        }
    }

    /**
     * block 一直到退出程序
     * @throws InterruptedException
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null){
            server.awaitTermination();
        }
    }

    public ServiceNode getZkServiceNode() {
        return zkServiceNode;
    }
}
