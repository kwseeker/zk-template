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

//TODO: 连接池优化，多服务连接管理
public abstract class AbstractServiceClient<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractServiceClient.class);

    private final ManagedChannel channel;
    private final GreetServiceGrpc.GreetServiceBlockingStub blockingStub;

    public AbstractServiceClient(String host,int port){
        channel = ManagedChannelBuilder.forAddress(host,port)
                .usePlaintext(true)
                .build();
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public  void greet(String name){
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try{
            response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e)
        {
            logger.warn("RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Message from gRPC-Server: "+response.getMessage());
    }
}
