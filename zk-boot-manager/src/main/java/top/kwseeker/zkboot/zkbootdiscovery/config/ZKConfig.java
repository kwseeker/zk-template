package top.kwseeker.zkboot.zkbootdiscovery.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 其实这个这些模块都是作为zookeeper的客户端
 * 即zk客户端的配置
 */
@Component
@ConfigurationProperties(prefix = "zk.server")
public class ZKConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ZKConfig.class);

    private List<String> addrs = new ArrayList<>();
    @Value("${zk.server.basePath}")
    private String basePath;
    @Value("${zk.client.maxRetry}")
    private int maxRetry;
    @Value("${zk.client.minSleepTimeMs}")
    private int minSleepTimeMs;
    @Value("${zk.client.maxSleepTimeMs}")
    private int maxSleepTimeMs;
    @Value("${zk.client.maxSleepTimeMs}")
    private int connectionTimeout;
    @Value("${zk.client.sessionTimeout}")
    private int sessionTimeout;

    {
        LOG.info("初始化设置默认配置参数");
        this.addrs.add("localhost:2181");
        this.basePath = "/zkBoot";
        this.maxRetry = 3;
        this.minSleepTimeMs = 5000;
        this.maxSleepTimeMs = 5000;
        this.connectionTimeout = 3000;
        this.sessionTimeout = 15000;
    }

    public List<String> getAddrs() {
        return addrs;
    }

    public void setAddrs(List<String> addrs) {
        this.addrs = addrs;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public int getMaxRetry() {
        return maxRetry;
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    public int getMinSleepTimeMs() {
        return minSleepTimeMs;
    }

    public void setMinSleepTimeMs(int minSleepTimeMs) {
        this.minSleepTimeMs = minSleepTimeMs;
    }

    public int getMaxSleepTimeMs() {
        return maxSleepTimeMs;
    }

    public void setMaxSleepTimeMs(int maxSleepTimeMs) {
        this.maxSleepTimeMs = maxSleepTimeMs;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(String addr : addrs) {
            sb.append(addr);
        }
        return "ZKConfig{" +
                "addrs=" + sb.toString() +
                ", basePath='" + basePath +
                ", maxRetry=" + maxRetry +
                ", minSleepTimeMs=" + minSleepTimeMs +
                ", maxSleepTimeMs=" + maxSleepTimeMs +
                ", connectionTimeout=" + connectionTimeout +
                ", sessionTimeout=" + sessionTimeout +
                '}';
    }
}
