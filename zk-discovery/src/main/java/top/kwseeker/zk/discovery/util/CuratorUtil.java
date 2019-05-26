package top.kwseeker.zk.discovery.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class CuratorUtil {

    private static final Logger LOG = LoggerFactory.getLogger(CuratorUtil.class);

    public static CuratorFramework createZkClient() {
        try {
            Properties props = PropertyUtil.load("zookeeper.properties");
            return CuratorFrameworkFactory.builder()
                    .connectString(props.getProperty("server.domain"))
                    .sessionTimeoutMs(Integer.valueOf(props.getProperty("server.port")))
                    .retryPolicy(new ExponentialBackoffRetry(1000,3))   //
                    .build();
        } catch (IOException e) {
            LOG.error("zk client create failed, e=" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
