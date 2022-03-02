package top.kwseeker.zk.configcenter.core;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import top.kwseeker.zk.configcenter.core.anno.ZkExtendConfigurable;
import top.kwseeker.zk.configcenter.core.anno.ZkFieldConfigurable;
import top.kwseeker.zk.configcenter.core.anno.ZkTypeConfigurable;
import top.kwseeker.zk.configcenter.core.exception.ConfigureException;
import top.kwseeker.zk.configcenter.core.listener.DataChangeListener;
import top.kwseeker.zk.configcenter.core.operator.Updater;
import top.kwseeker.zk.configcenter.core.resover.ExtendResolver;
import top.kwseeker.zk.configcenter.core.resover.Resolver;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 连接管理
 * 为标注了需要动态管理的配置，创建连接（一个ZK集群默认一个连接实例），
 * 然后
 */
@Slf4j
public class ZkRegister {

    private static final int SESSION_TIMEOUT_MS = 60 * 1000;
    private static final int CONN_TIMEOUT_MS = 5000;
    private static final int BASE_SLEEP_TIME_MS = 5000;
    private static final int MAX_RETRY_TIMES = 30;
    private static final boolean CAN_BE_READ_ONLY = true;
    public static final String DEFAULT_SERVERS = "localhost:2181";

    private static final RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRY_TIMES);
    private static final Map<String, CuratorFramework> clientCache = Collections.synchronizedMap(new WeakHashMap<>(5));

    private final String globalZkServers;        //全局的 zk server 节点配置，为了支持多套集群

    public ZkRegister(String globalZkServers) {
        this.globalZkServers = globalZkServers;
    }

    /**
     * 对 ZkTypeConfigurable 注解的类
     *
     * @param clazz         需要动态管理的配置类
     * @param forceWhenNull
     */
    public final synchronized void register(final Class<?> clazz, final boolean forceWhenNull) {
        if (!clazz.isAnnotationPresent(ZkTypeConfigurable.class)) {
            throw new ConfigureException("without necessary zk type configuration!");
        }

        ZkTypeConfigurable type = clazz.getAnnotation(ZkTypeConfigurable.class);
        final String servers = type.useOwnServers() ? type.servers() : globalZkServers;
        if (servers == null || servers.equals("")) {
            throw new ConfigureException("zk servers must not be null when use own servers!");
        }

        //1 创建Curator连接
        CuratorFramework curatorClient = makeZkClient(servers);

        //2 构造ZNode完整路径
        String rootPath = type.nodePath().trim();
        if ("".equals(rootPath)) {
            rootPath = clazz.getPackage().getName().replaceAll("\\.", "/");
        }
        final String classZNodePath = generateClassZNodePath(rootPath, clazz.getSimpleName());

        //3 遍历field，注册子节点监听
        final Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ZkFieldConfigurable.class)) {
                commonFieldHandler(curatorClient, field, classZNodePath, clazz, forceWhenNull);
                continue;
            }
            if (field.isAnnotationPresent(ZkExtendConfigurable.class)) {
                extendDataHandler(curatorClient, field, classZNodePath, clazz, forceWhenNull);
            }
        }
    }

    private void commonFieldHandler(final CuratorFramework zkClient, final Field field, final String classPath, final Class<?> clazz,
                                    final boolean forceWhenNull) {
        log.info("3> register common field:" + field.getName() + "type:" + field.getType().getSimpleName());

        ZkFieldConfigurable zkFieldConfigurable = field.getAnnotation(ZkFieldConfigurable.class);
        String fieldPath = "".equals(zkFieldConfigurable.nodePath()) ?
                generateClassZNodePath(classPath, field.getName()) : generateClassZNodePath(classPath, zkFieldConfigurable.nodePath());




        //String value = zkClient.readData(fieldPath, true);
        //log.debug("ZK PATH :" + fieldPath + " value:" + value);
        //
        ////resolver解析
        //Resolver<?> resolver;
        //try {
        //    resolver = zkFieldConfigurable.resolver().getConstructor(Class.class, Field.class).newInstance(clazz, f);
        //} catch (Exception e) {
        //    log.debug("get resolver fail!");
        //    return;
        //}
        ////订阅
        //subscribe(value, zkClient, fieldPath, field.update(), forceWhenNull, resolver);
    }


    private void extendDataHandler(final CuratorFramework zkClient, final Field field, final String classPath, final Class<?> clazz,
                                   final boolean forceWhenNull) throws Exception {
        log.info("3> register extend field:" + field.getName() + "type:" + field.getType().getSimpleName());

        ZkExtendConfigurable zkExtendConfigurable = field.getAnnotation(ZkExtendConfigurable.class);
        String fieldPath = "".equals(zkExtendConfigurable.extPath()) ?
                generateClassZNodePath(classPath, field.getName()) : generateClassZNodePath(classPath, zkExtendConfigurable.extPath());

        //createIfNeed(zkClient, fieldPath);

        String tempKey = zkExtendConfigurable.tempKey();
        Class<? extends ExtendDataStore<?>> store = zkExtendConfigurable.dataStore();
        try {
            ExtendResolver extendResolver = new ExtendResolver(tempKey, store.newInstance(), clazz, field);
            //订阅
            subscribe(value, zkClient, fieldPath, forceWhenNull, field.update(), extendResolver);
        } catch (InstantiationException e) {
            log.error("Instantiation Exception..", e);
        } catch (IllegalAccessException e) {
            log.error("Illegal Access Exception..", e);
        }
    }

    /**
     *
     * @param value
     * @param zkClient
     * @param fieldPath
     * @param forceWhenNull
     * @param update
     * @param resolver
     */
    private void subscribe(final String value,
                           final CuratorFramework curatorClient,
                           final String fieldPath,
                           final boolean forceWhenNull,
                           final boolean update,
                           final Resolver<?> resolver) throws Exception {

        if (value == null && !forceWhenNull) {
            return;
        } else if (value == null && forceWhenNull) {
            createIfNeed(curatorClient, fieldPath);
            String defaultValue = (String) resolver.get();
            curatorClient.setData().forPath(fieldPath, defaultValue.getBytes());
        } else {
            //设置值
            resolver.set(value);
        }

        //动态更新
        if (update) {
            Updater.register(fieldPath, resolver);
            //zk订阅
            curatorClient.subscribeDataChanges(fieldPath, new DataChangeListener());
        }
    }

    private void createIfNeed(CuratorFramework curatorClient, String path) throws Exception {
        Stat stat = curatorClient.checkExists().forPath(path);
        if (stat == null) {
            String s = curatorClient.create().creatingParentsIfNeeded().forPath(path);
            log.debug("path {} not exist, and created!", s);
        }
    }

    /**
     * 创建可复用的Curator客户端
     * 默认一个集群对应一个连接
     *
     * @param servers 服务器集群
     * @return CuratorFramework instance
     */
    private CuratorFramework makeZkClient(String servers) {
        if (clientCache.containsKey(servers)) {
            return clientCache.get(servers);
        }

        CuratorFramework curatorClient = CuratorFrameworkFactory.builder()
                .connectString(servers)
                .retryPolicy(retryPolicy)
                .sessionTimeoutMs(SESSION_TIMEOUT_MS)
                .connectionTimeoutMs(CONN_TIMEOUT_MS)
                .canBeReadOnly(CAN_BE_READ_ONLY)
                .build();

        curatorClient.getConnectionStateListenable().addListener((client, newState) -> {
            if (newState == ConnectionState.CONNECTED) {
                log.info("Connection to servers:{} succeed!", servers);
            }
        });
        curatorClient.start();

        clientCache.put(servers, curatorClient);
        log.info("1> create zk client done");
        return curatorClient;
    }

    private String generateClassZNodePath(String rootPath, String className) {
        final String separator = Constant.SEPARATOR;
        if (!rootPath.startsWith(separator)) {
            rootPath = separator + rootPath;
        }
        if (!rootPath.endsWith(separator)) {
            rootPath = rootPath + separator;
        }

        if (className.startsWith(separator)) {
            className = className.substring(1);
        }

        String classZNodePath = rootPath + className;
        log.info("2> generate zk class node done, nodePath={}", classZNodePath);
        return classZNodePath;
    }
}
