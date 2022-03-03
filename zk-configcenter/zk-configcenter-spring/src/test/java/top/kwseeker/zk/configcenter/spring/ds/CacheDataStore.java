package top.kwseeker.zk.configcenter.spring.ds;

import top.kwseeker.zk.configcenter.core.ExtendDataStore;

import java.util.HashMap;
import java.util.Map;

public class CacheDataStore implements ExtendDataStore<Map<String,String>> {

    public final static Map<String, Map<String,String>> CACHE = new HashMap<>(10);
    static {
        //init value
        Map<String,String> map = new HashMap<>(4);
        map.put("spring.datasource.url","jdbc:mysql://localhost:3306/test_db");
        map.put("spring.datasource.username","root");
        map.put("spring.datasource.password","123456");
        CACHE.put("dev",map);

        Map<String,String> map1 = new HashMap<>(4);
        map1.put("spring.datasource.url","jdbc:mysql://192.168.0.100:3306/test_db");
        map1.put("spring.datasource.username","user1");
        map1.put("spring.datasource.password","123456");
        CACHE.put("test",map1);

        Map<String,String> map2= new HashMap<>(4);
        map2.put("spring.datasource.url","jdbc:mysql://192.168.1.100:3306/test_db");
        map2.put("spring.datasource.username","user2");
        map2.put("spring.datasource.password","qzxg8t2cz2vubek7");
        CACHE.put("prod",map2);
    }

    @Override
    public void setValue(String key, Map<String, String> map) {
        CACHE.put(key,map);
    }

    @Override
    public Map<String, String> getValue(String key) {
        return CACHE.get(key);
    }
}
