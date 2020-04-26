package top.kwseeker.zk.apiUse;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * 节点的创建、读、写
 */
public class ZookeeperCurd {

    private final String connectString = "localhost:2181,localhost:2182,localhost:2183";
    private ZooKeeper zooKeeper;

    public ZookeeperCurd() {
        try {
            this.zooKeeper = new ZooKeeper(connectString, 5000, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void crudTest() throws KeeperException, InterruptedException {
        zooKeeper.create("/zk_crud", "1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zooKeeper.create("/zk_crud/test1", "11".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
                null, 3000);
        String data1 = new String(zooKeeper.getData("/zk_crud", false, null));
        zooKeeper.setData("/zk_crud", "2".getBytes(), -1);
        zooKeeper.exists("/zk_crud", false);
        zooKeeper.delete("/zk_crud/test1", -1);
        ZKUtil.deleteRecursive(zooKeeper, "/zk_crud");
        zooKeeper.close();
    }
}
