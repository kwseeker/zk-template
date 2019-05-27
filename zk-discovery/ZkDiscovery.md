# Zookeeper Discovery

## 代码结构设计

实际场景中，zookeeper服务是集群，有不同的域名/IP和端口；
针对每一个zookeeper节点应该设置一个连接（暂不考虑这个集群服务多个应用）
充分利用带宽，同时连接又不至于过多；每个连接为多个微服务提供服务注册和发现支持。

## 开发疑问

写代码时遇到很多问题，记录一下然后去源码找答案。

1）ServiceDiscovery 创建时为什么传 ServiceInstance ？传这个参数做什么的？
```
public ServiceDiscoveryImpl(CuratorFramework client, String basePath, InstanceSerializer<T> serializer, ServiceInstance<T> thisInstance, boolean watchInstances)
{
    this.watchInstances = watchInstances;
    this.client = Preconditions.checkNotNull(client, "client cannot be null");
    this.basePath = Preconditions.checkNotNull(basePath, "basePath cannot be null");
    this.serializer = Preconditions.checkNotNull(serializer, "serializer cannot be null");
    if ( thisInstance != null )
    {
        Entry<T> entry = new Entry<T>(thisInstance);
        entry.cache = makeNodeCache(thisInstance);
        services.put(thisInstance.getId(), entry);
    }
}
```
浏览ServiceDiscoveryImpl代码后，可以看到 services 就是注册的服务，而这里传参之后发现
通过put方法将 thisInstance 放到了 services 中，因此相当于注册。

2）ServiceDiscovery 和 CuratorFramework 的关系？
