package top.kwseeker.zk.configcenter.core.anno;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ZkTypeConfigurable {

    /**
     * 该配置在zk上面的节点路径（node path）
     * 缺省按照一下规则：
     * config/center/default/[applicationName]/[className]/
     */
    String path() default "";

    /**
     * 使用当前的zk servers 配置
     */
    boolean useOwnServer() default false;

    /**
     * 当前zk servers
     */
    String servers() default "";
}
