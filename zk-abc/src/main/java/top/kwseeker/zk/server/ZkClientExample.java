package top.kwseeker.zk.server;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

public class ZkClientExample {

    private static final String CLUSTER = "localhost:2184,localhost:2185,localhost:2186";

    private static final String PATH = "/test/zkclientexample";

    public static void main(String[] args) {
        ZkClient zkCli = new ZkClient(CLUSTER);
        CreateMode mode = CreateMode.PERSISTENT;
        String data = "test zkClient connection";
        String nodeName = zkCli.create(PATH, data, mode);
        System.out.println("new znode name: " + nodeName);
        Object readData = zkCli.readData(PATH);
        System.out.println("znode data: " + readData);
    }
}
