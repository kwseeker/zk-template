package top.kwseeker.zkboot.zkbootbusiservice.manager;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.zk.protobuf.GreetServiceGrpc;
import top.kwseeker.zk.protobuf.HelloReply;
import top.kwseeker.zk.protobuf.HelloRequest;

import java.util.concurrent.TimeUnit;

public class GreetServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(GreetServiceClient.class);

    private final ManagedChannel channel;
    private final GreetServiceGrpc.GreetServiceBlockingStub blockingStub;
    private final String host;
    private final int port;

    public GreetServiceClient(String host,int port){
        this.host = host;
        this.port = port;
        channel = ManagedChannelBuilder.forAddress(host,port)
                .usePlaintext(true)
                .build();
        blockingStub = GreetServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public String greet(String name){
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try{
            response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            logger.warn("RPC failed: {0}", e.getStatus());
            return "";
        }
        logger.info("Message from gRPC-Server: "+response.getMessage());
        return response.getMessage();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
