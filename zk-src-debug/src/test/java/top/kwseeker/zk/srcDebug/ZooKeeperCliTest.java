package top.kwseeker.zk.srcDebug;

import org.apache.zookeeper.cli.CliException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ZooKeeperCliTest {

    private ZooKeeperClient zooKeeperClient;

    @Before
    public void init() throws IOException {
        System.out.println("initialize ...");
        String[] args = new String[] {"-server","localhost:2181,localhost:2182,localhost:2183"};
        this.zooKeeperClient = new ZooKeeperClient(args);
    }

    @Test
    public void zkCmdInvoke() {
        try {
            String[] cmdArgs = new String[]{"ls", "-swR", "/zk_test"};
            zooKeeperClient.zkCmdInvoke(cmdArgs);
        } catch (CliException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void zkMainInvoke() {
        try {
            String[] args = new String[] {"-server","localhost:2181,localhost:2182,localhost:2183"};
            ZooKeeperClient.zkMainInvoke(args);
        } catch (Exception e) {
            System.out.println("Exception e=" + e.getMessage());
            e.printStackTrace();
        }
    }
}