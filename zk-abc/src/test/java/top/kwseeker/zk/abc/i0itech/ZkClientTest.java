package top.kwseeker.zk.abc.i0itech;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.junit.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ZkClientTest {

    private static final String CLUSTER_SERVERS = "127.0.0.1:2181";
    private static final int SESSION_TIMEOUT_MS = 60*1000;
    private static final int CONN_TIMEOUT_MS = 5000;
    private static final String PATH = "/abc/" + ZkClientTest.class.getName();

    private ZkClient zkClient;

    @Before
    public void init() {
        zkClient = createClient();
        log.info("create zkClient done");
    }

    @After
    public void release() {
        zkClient.close();
        log.info("release zkClient done");
    }

    @Test
    public void testBaseOperations() throws InterruptedException {
        //节点操作
        log.info(PATH);
        String value = zkClient.readData(PATH, true);
        if (value == null) {
            zkClient.createPersistent(PATH, true);
            zkClient.writeData(PATH, "defaultData");
        }

        zkClient.subscribeDataChanges(PATH, new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                log.info("handle data changed, dataPath={}, data={}", dataPath, data);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                log.info("handle data deleted, dataPath={}", dataPath);
            }
        });

        Thread.sleep(100*1000);
    }

    private ZkClient createClient() {
        return new ZkClient(CLUSTER_SERVERS, SESSION_TIMEOUT_MS, CONN_TIMEOUT_MS, new StringZkSerializer());

    }

    static class StringZkSerializer implements ZkSerializer {

        private final Charset charset = StandardCharsets.UTF_8;

        @Override
        public byte[] serialize(Object data) throws ZkMarshallingError {
            return data.toString().getBytes(this.charset);
        }

        @Override
        public Object deserialize(byte[] bytes) throws ZkMarshallingError {
            return new String(bytes, this.charset);
        }
    }
}
