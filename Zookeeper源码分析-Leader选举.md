# Zookeeper源码分析-Leader选举

## 源码准备

源码结构

```
.
├── bin							//启动脚本
├── build.xml
├── checkstyleSuppressions.xml
├── checkstyle.xml
├── conf						//配置文件
├── dev
├── excludeFindBugsFilter.xml
├── ivysettings.xml
├── ivy.xml
├── LICENSE.txt
├── NOTICE.txt
├── owaspSuppressions.xml
├── parent.iml
├── pom.xml
├── README.md
├── README_packaging.txt
├── target
├── zookeeper-assembly
├── zookeeper-client				//C语言实现的客户端源码
├── zookeeper-compatibility-tests
├── zookeeper-contrib
├── zookeeper-docs
├── zookeeper-it
├── zookeeper-jute					//序列化代码
├── zookeeper-metrics-providers
├── zookeeper-recipes				//示例代码 recipes:食谱
└── zookeeper-server				//服务端源码
```

添加一个类

```java
package org.apache.zookeeper.version;

public interface Info {
    int MAJOR = 1;
    int MINOR = 0;
    int MICRO = 0;
    String QUALIFIER = null;
    int REVISION = -1;
    String REVISION_HASH = "1";
    String BUILD_DATE = "2020‐10‐15";
}
```

编译启动

```shell
# 跳过编译和运行测试代码
mvn clean package -Dmaven.test.skip=true
```

找到代码入口，可以在bin/zkServer.sh 中加 `set -x`；然后执行一下。可以看到入口

```shell
nohup /Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/bin/java 
    -Dzookeeper.log.dir=/opt/apache-zookeeper-3.5.5-bin/bin/../logs 
    -Dzookeeper.log.file=zookeeper-lee-server-LeeMB.local.log 
    -Dzookeeper.root.logger=INFO,CONSOLE 
    -XX:+HeapDumpOnOutOfMemoryError 
    '-XX:OnOutOfMemoryError=kill -9 %p' 
    -cp '/opt/apache-zookeeper-3.5.5-bin/bin/../zookeeper-server/target/classes:/opt/apache-zookeeper-3.5.5-bin/bin/../build/classes:/opt/apache-zookeeper-3.5.5-bin/bin/../zookeeper-server/target/lib/*.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../build/lib/*.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../lib/zookeeper-jute-3.5.5.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../lib/zookeeper-3.5.5.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../lib/slf4j-log4j12-1.7.25.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../lib/slf4j-api-1.7.25.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../lib/netty-all-4.1.29.Final.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../lib/log4j-1.2.17.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../lib/json-simple-1.1.1.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../lib/jline-2.11.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../lib/jetty-util-9.4.17.v20190418.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../lib/jetty-servlet-9.4.17.v20190418.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../lib/jetty-server-9.4.17.v20190418.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../lib/jetty-security-9.4.17.v20190418.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../lib/jetty-io-9.4.17.v20190418.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../lib/jetty-http-9.4.17.v20190418.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../lib/javax.servlet-api-3.1.0.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../lib/jackson-databind-2.9.8.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../lib/jackson-core-2.9.8.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../lib/jackson-annotations-2.9.0.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../lib/commons-cli-1.2.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../lib/audience-annotations-0.5.0.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../zookeeper-*.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../zookeeper-server/src/main/resources/lib/*.jar:/opt/apache-zookeeper-3.5.5-bin/bin/../conf:' 
    -Xmx1000m -Dcom.sun.management.jmxremote 
    -Dcom.sun.management.jmxremote.local.only=false 
    org.apache.zookeeper.server.quorum.QuorumPeerMain 
    /opt/apache-zookeeper-3.5.5-bin/bin/../conf/zoo.cfg
```

Main入口：QuorumPeerMain，传参： zoo.cfg。

服务端启动：（添加一个启动配置）

```
Main class: org.apache.zookeeper.server.quorum.QuorumPeerMain
Program arguments: /home/lee/mywork/java/distribution/zookeeper/lib-sources/zookeeper/conf/zoo.cfg
Vm options: 选填
```

本地伪集群启动：

按上面的多配置两个，端口设置为不一样即可。

客户端启动：

```shell
Main class: org.apache.zookeeper.ZooKeeperMain
Program arguments: -server localhost:2181
```

日志输出配置



## Leader选举流程

选票核心参数：myid, zxid。

顺序启动zk1、zk2、zk3三个节点的Leader选举流程：





