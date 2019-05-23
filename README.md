# zk-template

zookeeper常用应用场景可复用模块实现

关于zookeeper原理查看有道云笔记，后面移到kwseeker/development-notes。

## 常见使用场景

+ **统一命名服务/服务注册与发现**

+ **配置中心**（ZK的数据发布订阅功能）

+ **负载均衡**（ZK的负载均衡算法）

+ **分布式锁**  
    实现原理：  
    排他锁的实现原理：利用在同一个节点下创建新的临时节点只有一个能成功的性质。  

+ **队列管理**

+ **集群(Worker)管理**  
    添加剔除Worker，设置通信hosts列表。

+ **集群监控**

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

文档都是很无力的，更高级的使用还是看源码实现和测试。

## Zookeeper应用实现

官方实例：[Example](https://curator.apache.org/curator-examples/index.html)

