package top.kwseeker.zkboot.zkbootbusiservice.service;

import org.springframework.stereotype.Service;
import top.kwseeker.zk.common.service.GreetService;

@Service
public class GreetServiceImpl implements GreetService {

    @Override
    public String sayHello(String name) {
        //通过连接池获取连接,如果无此服务的连接，主动请求zookeeper查找服务并创建本地连接
        //GRPC服务有什么特点，如何合理地缓存连接，还要考虑负载均衡问题
        //首先为了负载均衡肯定需要把zk中注册的此服务所有的服务实例缓存起来，
        //服务实例节点从zk中增加或被删除时，zk应该主动通知微服务消费者

    }
}
