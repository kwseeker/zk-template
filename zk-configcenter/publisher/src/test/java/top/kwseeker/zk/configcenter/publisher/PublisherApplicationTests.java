package top.kwseeker.zk.configcenter.publisher;

import org.apache.curator.framework.CuratorFramework;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PublisherApplicationTests {

    @Autowired
    private CuratorFramework zkClient;

    @Test
    void testCuratorFramework() throws Exception {
        zkClient.create()
                .creatingParentsIfNeeded()
                .forPath("/aaa/aa/a");
    }

}
