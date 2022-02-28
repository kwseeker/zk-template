package top.kwseeker.zk.configcenter.core.connection;

public interface ZkDataListener {

    /**
     * 当监听的节点发送数据变动时出发该方法
     *
     * @param dataPath  节点路径
     * @param newData   ？
     */
    void onDataChanged(String dataPath, byte[] newData);

    /**
     * 当监听的节点数据被删除时触发
     *
     * @param dataPath 节点路径
     */
    void onDataDeleted(String dataPath);
}
