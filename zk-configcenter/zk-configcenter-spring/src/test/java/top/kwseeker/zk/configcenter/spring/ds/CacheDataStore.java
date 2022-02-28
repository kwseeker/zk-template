package top.kwseeker.zk.configcenter.spring.ds;

import top.kwseeker.zk.configcenter.core.ExtendDataStore;

import java.util.HashMap;
import java.util.Map;

public class CacheDataStore implements ExtendDataStore<Map<String,String>> {

    public final static Map<String, Map<String,String>> CACHE = new HashMap<String, Map<String,String>>(10);
    static {
        //init value
        Map<String,String> map = new HashMap<String, String>(4);
        map.put("d1","d1");
        map.put("d2","d2");
        map.put("d3","d3");
        map.put("d4","d4");
        CACHE.put("key_words",map);

        Map<String,String> map1 = new HashMap<String, String>(4);
        map1.put("a1", "a1");
        map1.put("a2","a2");
        map1.put("a3","a3");
        map1.put("a4","a4");
        CACHE.put("key_words1",map1);

        Map<String,String> map2= new HashMap<String, String>(4);
        map2.put("咚咚1","咚咚1");
        map2.put("咚咚2","咚咚2");
        map2.put("咚咚3","咚咚3");
        map2.put("咚咚4","咚咚4");
        CACHE.put("key_words2",map2);
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
