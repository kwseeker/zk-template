package top.kwseeker.zk.configcenter.core;

/*
* 核心模块：
*   主要就是创建ZK连接，然后增删改查ZNode，以及监听ZNode变更
*
* 功能：
*   1）支持多集群
*
* 代码在别人的开源项目ucc上修改
*   主要修改：
*   1）替换ZkClient为Curator
*   2) 服务节点动态扩/缩容     TODO
* */