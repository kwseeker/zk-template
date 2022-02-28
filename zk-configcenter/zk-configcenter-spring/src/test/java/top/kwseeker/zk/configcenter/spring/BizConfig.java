package top.kwseeker.zk.configcenter.spring;

import org.springframework.stereotype.Service;
import top.kwseeker.zk.configcenter.core.anno.ZkExtendConfigurable;
import top.kwseeker.zk.configcenter.core.anno.ZkTypeConfigurable;
import top.kwseeker.zk.configcenter.spring.ds.CacheDataStore;

import java.util.HashMap;
import java.util.Map;

@Service
@ZkTypeConfigurable(useOwnServers = false, path = "/conf/test/demo")
public class BizConfig implements Config {

    @ZkExtendConfigurable(path = "keyWords", update = true, tempKey = "key_words", dataStore = CacheDataStore.class)
    public static Map<String, String> config = new HashMap<>(0);

    @Override
    public void print() {
        System.out.println(config);
    }
}
