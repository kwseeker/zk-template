package top.kwseeker.zk.configcenter.core.operator;

import top.kwseeker.zk.configcenter.core.Resolver;
import top.kwseeker.zk.configcenter.core.exception.ConfigureException;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class Updater {
    private static final Map<String, Resolver<?>> map = Collections.synchronizedMap(new WeakHashMap<>(5));

    /**
     * 注册一个更新
     */
    public static void register(String path, Resolver<?> resolve){
        map.put(path, resolve);
    }

    public static void update(String path, String value) {
        Resolver<?> resolver = map.get(path);
        if(resolver == null) {
            throw new ConfigureException("unknown resolver find:[path" + path +"]");
        }
        resolver.set(value);
    }
}
