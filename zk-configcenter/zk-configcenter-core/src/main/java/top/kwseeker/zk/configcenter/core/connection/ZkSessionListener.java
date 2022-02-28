//package top.kwseeker.zk.configcenter.core.connection;
//
//import org.apache.zookeeper.KeeperException;
//
//public interface ZkSessionListener {
//
//    /**
//     * 监听会话创建（zkClient连接server）、会话超时重连
//     * <p>
//     *     <b>所有：EPHEMERAL类型节点，节点：exists/getData/getChildren 的所有watchers在session重连后会被再次创建 </b>
//     * </p>
//     * @param connection ZkClientConnection
//     * @throws org.apache.zookeeper.KeeperException if any
//     * @throws InterruptedException if any
//     */
//    void onSessionCreated(ZkClientConnection connection) throws KeeperException, InterruptedException;
//}
