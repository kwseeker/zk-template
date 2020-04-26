package top.kwseeker.zk.srcDebug;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeperMain;
import org.apache.zookeeper.cli.*;
import org.apache.zookeeper.client.ConnectStringParser;
import org.apache.zookeeper.client.StaticHostProvider;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ZooKeeperClient {

    private MyCommandOptions commandOptions = new MyCommandOptions();
    private ZooKeeper zk;
    static final Map<String, CliCommand> commandMapCli = new HashMap<>();

    static {
        new CloseCommand().addToMap(commandMapCli);
        new CreateCommand().addToMap(commandMapCli);
        new DeleteCommand().addToMap(commandMapCli);
        new DeleteAllCommand().addToMap(commandMapCli);
        // Depricated: rmr
        new DeleteAllCommand("rmr").addToMap(commandMapCli);
        new SetCommand().addToMap(commandMapCli);
        new GetCommand().addToMap(commandMapCli);
        new LsCommand().addToMap(commandMapCli);
        new Ls2Command().addToMap(commandMapCli);
        new GetAclCommand().addToMap(commandMapCli);
        new SetAclCommand().addToMap(commandMapCli);
        new StatCommand().addToMap(commandMapCli);
        new SyncCommand().addToMap(commandMapCli);
        new SetQuotaCommand().addToMap(commandMapCli);
        new ListQuotaCommand().addToMap(commandMapCli);
        new DelQuotaCommand().addToMap(commandMapCli);
        new AddAuthCommand().addToMap(commandMapCli);
        new ReconfigCommand().addToMap(commandMapCli);
        new GetConfigCommand().addToMap(commandMapCli);
        new RemoveWatchesCommand().addToMap(commandMapCli);
    }

    public ZooKeeperClient(String[] args) throws IOException {
        commandOptions.parseOptions(args);
        String connectString = commandOptions.getOption("server");
        zk = new ZooKeeper(connectString,
                3000,
                new MyWatcher(),
                false,
                new StaticHostProvider(new ConnectStringParser(connectString).getServerAddresses()),
                null);
    }

    public void zkCmdInvoke(String[] args) throws CliException, IOException, InterruptedException {
        commandOptions.parseOptions(args);
        processZKCmd(commandOptions);
    }

    protected boolean processZKCmd(MyCommandOptions co) throws CliException, IOException, InterruptedException {
        String[] args = co.getArgArray();
        String cmd = co.getCommand();
        if (args.length < 1) {
            throw new MalformedCommandException("No command entered");
        }

        boolean watch = false;
        System.out.println("Processing " + cmd);
        if (cmd.equals("quit")) {
            zk.close();
            System.exit(0);
        } else if (cmd.equals("redo") && args.length >= 2) {
            //获取输入命令历史数据，重新执行指定命令

        } else if (cmd.equals("history")) {
            //打印输入命令历史数据

        } else if (cmd.equals("printwatches")) {
            //开启和关闭监听器

        } else if (cmd.equals("connect")) {
            //重新连接
        }

        // Below commands all need a live connection
        if (zk == null || !zk.getState().isAlive()) {
            System.out.println("Not connected");
            return false;
        }
        // execute from commandMap
        CliCommand cliCmd = commandMapCli.get(cmd);
        if(cliCmd != null) {
            cliCmd.setZk(zk);
            watch = cliCmd.parse(args).exec();
        }
        return watch;
    }

    /**
     * ZooKeeperMain调用run方法
     * @param args 连接参数
     * @throws Exception
     */
    public static void zkMainInvoke(String[] args) throws Exception {
        ZooKeeperMain main = new ZooKeeperMain(args);
        Method runMethod = ZooKeeperMain.class.getDeclaredMethod("run");
        runMethod.setAccessible(true);
        runMethod.invoke(main);
    }
}
