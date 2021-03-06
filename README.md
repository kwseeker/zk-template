# zk-template

zookeeper常用应用场景可复用模块实现

关于zookeeper原理查看有道云笔记，后面移到kwseeker/development-notes。

## 常见使用场景

+ **统一命名服务/服务注册与发现**

    - 企业级的服务与发现架构
    
        ![](https://upload-images.jianshu.io/upload_images/10299630-2f0f0fa38fdb9d9a?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
        就是图中zuul、Eureka、service部分。实际的架构中这里面每一个部分都可能是一个集群。
        而服务注册到哪个Eureka节点（最终都会同步到其他节点），一个微服务节点调另一个微服务的接口具体是怎么分派到其中一个节点的？
        之前看Eureka得知其内部有一套负载均衡策略（集成了Ribbon）。猜想负载均衡应该存在系统的各个集群中。
        
    - Zookeeper实现服务注册与发现的原理
    
        ![Zookeeper实现服务注册与发现](https://upload-images.jianshu.io/upload_images/2038379-05931473aa8bc6b9.jpg?imageMogr2/auto-orient/)
        Zookeeper实现服务注册与发现就是使用其节点监听功能。对应于Curator客户端就是 PathChildrenCacheListener。
        
        每个微服务都是一个zookeeper的客户端，作为服务生产者可以向zookeeper 注册节点，而这个节点的数据就是微服务本身可以提供的服务接口等信息；
        同时它也可以监听其他客户端发布的信息，作为一个服务消费者监听各节点数据变化，并更新本地服务列表。  

    - 实现方案
        
        通过查找资料以及搜索github上别人的实现，总结出一套实现方案。
        
        Apache Curator 官方源码提供的服务注册与发现最简实现。
        https://github.com/apache/curator/blob/master/curator-examples/src/main/java/discovery/DiscoveryExample.java
        
        设计架构时需要考虑的几个问题（Eureka很常见问题）：  
        1）如果zookeeper节点和每个微服务节点都有多个，zk1...zkn, serv1_1...serv1_n, serv2_1...serv2_n;
        负载均衡应该如何实现？  
        2）集群中某节点故障，如何剔除或者恢复（失败重试机制）？  
           
        zookeeper实现服务注册与发现架构  
        1）zookeeper部署三台；  
        2）做两个微服务，每个微服务两个节点；
        3）监控集群状态怎么做？
                
    - 对比Eureka、Consul的实现原理
    
    - 为何Eureka比Zookeeper、Consul更适合做服务发现与注册
    
        [阿里巴巴为什么不用 ZooKeeper 做服务发现？](http://jm.taobao.org/2018/06/13/%E5%81%9A%E6%9C%8D%E5%8A%A1%E5%8F%91%E7%8E%B0%EF%BC%9F/)
    
+ **配置中心**（ZK的数据发布订阅功能）

+ **负载均衡**（ZK的负载均衡算法）

+ **分布式锁**  
    实现原理：  
    排他锁的实现原理：利用在同一个节点下创建新的临时节点只有一个能成功的性质。  

+ **队列管理**

+ **集群(Worker)管理**  
    添加剔除Worker，设置通信hosts列表。

+ **集群监控**

    实现分布式进程状态监控；
    比如：使用临时节点特性监控进程是否还在运行，进程如果还在运行会维持连接心跳，
    如果进程退出，无心跳连接后会删除临时节点。
    

## 服务端安装配置启动

选择使用docker部署zookeeper https://hub.docker.com/_/zookeeper/

单机模式配置 zookeeper根目录下创建 conf/zoo.cfg
```
tickTime=2000               #用作心跳周期
dataDir=/var/lib/zookeeper  #存放数据库快照和数据库更新日志的目录
clientPort=2181             #客户端连接监听端口
```

主从复制模式配置
```
tickTime=2000               #用作心跳周期
dataDir=/var/lib/zookeeper  #存放数据库快照和数据库更新日志的目录
clientPort=2181             #客户端连接监听端口
initLimit=5                 #连接Leader的超时时间
syncLimit=2                 #其他ZK服务器连接到Leader的过期时间
server.1=zoo1:2888:3888     #2888是出端口，3888是入端口
server.2=zoo2:2888:3888
server.3=zoo3:2888:3888
```
如果部署在一台机器上则修改出入端口。
    
启动
```
bin/zkServer.sh start
```

## 导入客户端Apache curator依赖

Github地址 [apache/curator](https://github.com/apache/curator)

[Curator API](http://curator.apache.org/apidocs)

文档都是很无力的，更高级的使用还是看源码实现和测试。

## Zookeeper应用实现

官方实例：[Example](https://curator.apache.org/curator-examples/index.html)

### 1 服务发现与注册

spring boot 中应用 zookeeper 参考 zk-springboot-web

#### 功能需求：  
1）命名服务（服务注册、注销与更新）  
2）服务监控（如何做出类似Eureka节点监控页面） 
3）微服务注册与发现，配合web微服务实例通过Grpc相互调用测试  
4）微服务本地维护服务列表Guava缓存，通过zookeeper的消息订阅与发布功能实现服务列表的更新  
5）在web客户端与微服务之间构建路由层，所有客户端均通过路由层路由到具体的服务  
6）实现分布式服务器动态上下线感知

#### 软件架构：  
+ 客户端（M个）                 (9041-?)
+ 路由层（暂时做一个路由节点）    (9031)
+ 微服务层（N个服务节点，互相通过rpc调用）  
    zk-boot-busiservice     (9021)
    zk-boot-basicservice    (9011)
+ Zookeeper层（微服务管理）
    zk-boot-discovery       (9001)
    
#### 具体细节的实现

+  微服务应用与Zookeeper集群之间连接方案
    
    1）微服务应用直接作为Zookeeper集群的客户端（最简单的实现）
    2）微服务应用与Zookeeper集群之间添加一个中间层，微服务通过中间层进行服务注册与发现
    通过http访问实现服务发现，通过rabbitMQ消息订阅发布实现向微服务消费端广播服务列表更新消息；  
    3）取前两者中间，添加一个中间层负责提供服务发现，每个微服务本身作为zookeeper的client，实现
    服务注册与服务注销。