package top.kwseeker.zkboot.zkbootdiscovery;

/**
 * TODO: 改为从配置中心加载配置
 */
public class ZKConfig {

    public static final int ZK_MAX_RETRY = 3;
    public static final int ZK_MIN_TIME = 5000;
    public static final int ZK_MAX_TIME = 5000;
    public static final int ZK_SESSION_TIMEOUT = 5000;
    public static final int ZK_CONNECTION_TIMEOUT = 5000;
    public static final String ZK_DEFAULT_CACHE_PATH = "/";

    private String hosts;
    private String digest;
    private String namespace;
    private int maxRetries = ZK_MAX_RETRY;
    private int baseSleepTimeMs = ZK_MIN_TIME;
    private int maxSleepMs = ZK_MAX_TIME;
    private int sessionTimeout = ZK_SESSION_TIMEOUT;
    private int connectionTimeout = ZK_CONNECTION_TIMEOUT;
    private String watchPath = ZK_DEFAULT_CACHE_PATH;

    public ZKConfig(String hosts) {
        this.hosts = hosts;
    }

    public static ZKConfig build() {
        //zk.server.addr[0]=127.0.0.1:2181
        //#zk.server.addr[1]=127.0.0.1:2182
        //#zk.server.addr[2]=127.0.0.1:2183
        //zk.server.basePath=/zkBoot
        //zk.client.maxRetry=3
        //zk.client.minSleepTimeMs=5000
        //zk.client.maxSleepTimeMs=5000
        //zk.client.connectionTimeout=5000
        //zk.client.sessionTimeout=15000
        return new ZKConfig("127.0.0.1:2181")
                .setConnectionTimeout(5000)
                .setWatchPath("/zkBoot")
                .setDigest("zkboot")
                .setMaxRetries(3)
                .setMaxSleepMs(5000)
                .setBaseSleepTimeMs(5000)
                .setNamespace("zkboot")
                .setSessionTimeout(15000);
    }

    public String getHosts() {
        return hosts;
    }

    public ZKConfig setHosts(String hosts) {
        this.hosts = hosts;
        return this;
    }

    //public String getNamespace() {
    //    return namespace;
    //}
    //
    //public ZKConfig setNamespace(String namespace) {
    //    this.namespace = namespace;
    //    return this;
    //}

    public int getMaxRetries() {
        return maxRetries;
    }

    public ZKConfig setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    public int getBaseSleepTimeMs() {
        return baseSleepTimeMs;
    }

    public ZKConfig setBaseSleepTimeMs(int baseSleepTimeMs) {
        this.baseSleepTimeMs = baseSleepTimeMs;
        return this;
    }

    public int getMaxSleepMs() {
        return maxSleepMs;
    }

    public ZKConfig setMaxSleepMs(int maxSleepMs) {
        this.maxSleepMs = maxSleepMs;
        return this;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public ZKConfig setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
        return this;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public ZKConfig setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public String getDigest() {
        return digest;
    }

    public ZKConfig setDigest(String digest) {
        this.digest = digest;
        return this;
    }

    public String getWatchPath() {
        return watchPath;
    }

    public ZKConfig setWatchPath(String watchPath) {
        this.watchPath = watchPath;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public ZKConfig setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    @Override
    public String toString() {
        return "ZKConfig{" +
                "hosts='" + hosts + '\'' +
                //", digest='" + digest + '\'' +
                //", namespace='" + namespace + '\'' +
                ", maxRetries=" + maxRetries +
                ", baseSleepTimeMs=" + baseSleepTimeMs +
                ", maxSleepMs=" + maxSleepMs +
                ", sessionTimeout=" + sessionTimeout +
                ", connectionTimeout=" + connectionTimeout +
                ", watchPath='" + watchPath + '\'' +
                '}';
    }
}
