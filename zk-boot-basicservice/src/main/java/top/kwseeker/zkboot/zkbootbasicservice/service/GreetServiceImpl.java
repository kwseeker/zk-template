package top.kwseeker.zkboot.zkbootbasicservice.service;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.zk.protobuf.GreetServiceGrpc;
import top.kwseeker.zk.protobuf.HelloReply;
import top.kwseeker.zk.protobuf.HelloRequest;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(GreetServiceImpl.class);

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver){
        HelloReply reply = HelloReply.newBuilder().setMessage(("Hello "+req.getName())).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
        LOG.info("Message from gRPC-Client:" + req.getName());
    }
}
