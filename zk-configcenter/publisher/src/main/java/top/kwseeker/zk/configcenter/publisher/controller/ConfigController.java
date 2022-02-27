package top.kwseeker.zk.configcenter.publisher.controller;

//import org.I0Itec.zkclient.ZkClient;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private CuratorFramework zkClient;

    //读取元配置，包括服务实例名称，配置文件路径等
    //@PostMapping("/meta")
    //public String readMeta() {
    //    //TODO:通过命名服务获取这些信息
    //    return "";
    //}

    //读取某个配置文件对应znode的内容
    @GetMapping("/read")
    public String readConfigFile(@RequestParam("path") String nodePath) throws Exception {
        return new String(zkClient.getData().forPath(nodePath));
    }

    //将配置写回到znode
    @PostMapping("/write")
    public String writeConfigFile(@RequestParam("content") String nodePath, @RequestParam("content") String content) throws Exception {
        zkClient.setData().forPath(nodePath, content.getBytes());
        return "done";
    }
}
