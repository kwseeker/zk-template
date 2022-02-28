package top.kwseeker.zk.configcenter.core.anno;

import top.kwseeker.zk.configcenter.core.ExtendDataStore;

import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZkExtendConfigurable {
    /**
     * 该配置在zk上面的节点路径（node path）
     * 缺省按照一下规则：
     * config/center/default/applicationName/className/[field]
     */
    String path() default "";

    /**
     * 是否自动更新配置，默认是true
     */
    boolean update() default true;

    /**
     * 扩展配置的地址信息，用于找到真正的配置信息，存储在zookeeper上
     */
    String tempKey();

    /**
     * 用于实现扩展存储数据操作的类,默认给出redis的操作方案（应该是不给出，并且不许指定的项）
     */
    Class<? extends ExtendDataStore> dataStore() ;
}