package top.kwseeker.zkboot.zkbootbusiservice.manager;

import org.springframework.stereotype.Component;

/**
 * 服务名单及连接池管理
 *
 * 监听zk某服务节点下子节点的变化，有节点增加或者删除，要更新服务实例名单，如果是删除还要从连接池将连接释放；
 * 同时要更新连接池（使用时再根据服务名单创建实例，比如GreetService本来有一个服务实例，后来zk中又注册了两个服务实例，
 * 先只更新实例名单，然后某次调用这个服务，根据实例名单随机到一个服务实例，如果和这个服务实例的连接已经建立，直接使用
 * 未建立则建立连接，并将连接缓存到连接池）
 */
@Component
public class ClientManager implements ClientManage {

}
