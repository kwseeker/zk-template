package top.kwseeker.zk.configcenter.core;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import top.kwseeker.zk.configcenter.core.anno.ZkExtendConfigurable;
import top.kwseeker.zk.configcenter.core.anno.ZkFieldConfigurable;
import top.kwseeker.zk.configcenter.core.anno.ZkTypeConfigurable;
import top.kwseeker.zk.configcenter.core.exception.ConfigureException;
import top.kwseeker.zk.configcenter.core.listener.DataChangeListener;
import top.kwseeker.zk.configcenter.core.operator.Updater;
import top.kwseeker.zk.configcenter.core.resover.ExtendResolver;
import top.kwseeker.zk.configcenter.core.utils.StringZkSerializer;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

@Slf4j
public class ZkRegister {

    private static final Map<String, ZkClient> cache = Collections.synchronizedMap(new WeakHashMap<>(5));

    private final String globalZkServer;        //全局的zk server 配置
    private final static int timeOut = 50000;

    public ZkRegister(String globalZkServer) {
        this.globalZkServer = globalZkServer;
    }

    public final synchronized void register(final Class<?> clazz, final boolean forceWhenNull) {
        if (!clazz.isAnnotationPresent(ZkTypeConfigurable.class)) {
            throw new ConfigureException("Not Register!");
        }

        ZkTypeConfigurable type = clazz.getAnnotation(ZkTypeConfigurable.class);
        final String server = type.useOwnServer() ? type.servers() : globalZkServer;
        if (server == null || server.equals("")) {
            log.info("zk server must not null!");
            System.exit(0);
        }

        ZkClient zkClient = makeZkClient(server, timeOut);
        String root = type.path().trim();
        if ("".equals(root)) {
            root = clazz.getPackage().getName().replaceAll("\\.", "/");
        }
        //构造zk 路径
        final String path = getZkPath(root, clazz.getSimpleName());

        //开始遍历field
        final Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            if (f.isAnnotationPresent(ZkFieldConfigurable.class)) {
                commonFieldHandler(zkClient, f, path, clazz, forceWhenNull);
                continue;
            }
            if (f.isAnnotationPresent(ZkExtendConfigurable.class)) {
                extendDataHandler(zkClient, f, path, clazz, forceWhenNull);
            }
        }
    }

    private void commonFieldHandler(final ZkClient zkClient, final Field f, final String path, final Class<?> clazz, final boolean forceWhenNull) {
        log.debug("field:" + f.getName() + "type:" + f.getType().getSimpleName());
        ZkFieldConfigurable field = f.getAnnotation(ZkFieldConfigurable.class);

        String fieldPath = "".equals(field.path()) ? getZkPath(path, f.getName()) : getZkPath(path, field.path());
        String value = zkClient.readData(fieldPath, true);
        log.debug("ZK PATH :" + fieldPath + " value:" + value);

        //resolver解析
        Resolver<?> resolver;
        try {
            resolver = field.resolver().getConstructor(Class.class, Field.class).newInstance(clazz, f);
        } catch (Exception e) {
            log.debug("get resolver fail!");
            return;
        }
        //订阅
        subscribe(value, zkClient, fieldPath, field.update(), forceWhenNull, resolver);
    }

    private void extendDataHandler(final ZkClient zkClient, final Field f, final String path, final Class<?> clazz, final boolean forceWhenNull) {
        log.debug("field:" + f.getName() + "type:" + f.getType().getSimpleName());
        ZkExtendConfigurable field = f.getAnnotation(ZkExtendConfigurable.class);
        String fieldPath = "".equals(field.path()) ? getZkPath(path, f.getName()) : getZkPath(path, field.path());
        String value = zkClient.readData(fieldPath, true);
        log.debug("ZK PATH :" + fieldPath + " value:" + value);

        String tempKey = field.tempKey();
        Class<? extends ExtendDataStore> store = field.dataStore();
        try {
            ExtendResolver extendResolver = new ExtendResolver(tempKey, store.newInstance(), clazz, f);
            //订阅
            subscribe(value, zkClient, fieldPath, forceWhenNull, field.update(), extendResolver);
        } catch (InstantiationException e) {
            log.debug("Instantiation Exception..", e);
        } catch (IllegalAccessException e) {
            log.debug("Illegal Access Exception..", e);
        }
    }

    private void subscribe(final String value,
                           final ZkClient zkClient,
                           final String fieldPath,
                           final boolean forceWhenNull,
                           final boolean update,
                           final Resolver<?> resolver) {
        if (value == null && !forceWhenNull) {
            return;
        } else if (value == null && forceWhenNull) {
            zkClient.createPersistent(fieldPath, true);
            String defaultValue = (String) resolver.get();
            zkClient.writeData(fieldPath, defaultValue);
        } else {
            //设置值
            resolver.set(value);
        }

        //动态更新
        if (update) {
            Updater.register(fieldPath, resolver);
            //zk订阅
            zkClient.subscribeDataChanges(fieldPath, new DataChangeListener());
        }
    }

    private String getZkPath(String parent, String pathName) {
        final String separator = Constant.SEPARATOR;
        if (!parent.startsWith(separator)) {
            parent = separator + parent;
        }
        if (!parent.endsWith(separator)) {
            parent = parent + separator;
        }
        if (pathName.startsWith(separator)) {
            pathName = pathName.substring(1);
        }
        return parent + pathName;
    }

    private ZkClient makeZkClient(String server, int timeOut) {
        if (cache.containsKey(server)) {
            return cache.get(server);
        }

        final ZkClient zkClient = new ZkClient(server, timeOut, timeOut, new StringZkSerializer());
        cache.put(server, zkClient);
        return zkClient;
    }
}
