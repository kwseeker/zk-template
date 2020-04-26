package top.kwseeker.zk.apiUse;

import org.apache.zookeeper.KeeperException;
import org.junit.Test;

public class ApiUseTest {

    @Test
    public void crudTest() {
        try {
            new ZookeeperCurd().crudTest();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
