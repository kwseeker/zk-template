package top.kwseeker.zkboot.service;

import java.util.Map;
import java.util.UUID;

/**
 * 服务节点信息
 */
public class ServiceNode {

    private static final String ROOT = "/grpcservice";

    private String host;
    private int port;
    //下面三个属性不进行序列化
    private transient String serviceName;
    private transient boolean persistent;
    private transient String nodeId;
    //拓展参数
    private Map<String, Object> attrs = null;

    public ServiceNode(String host, int port, String serviceName, boolean persistent) {
        this.host = host;
        this.port = port;
        this.serviceName = serviceName;
        this.persistent = persistent;
        this.nodeId = nodeId();
    }

    public String nodePath() {
        return ROOT + "/" + serviceName + "/" + nodeId;
    }

    public String nodeId() {
        if(nodeId == null) {
            nodeId = UUID.randomUUID().toString();
        }
        return nodeId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public Map<String, Object> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, Object> attrs) {
        this.attrs = attrs;
    }
}
