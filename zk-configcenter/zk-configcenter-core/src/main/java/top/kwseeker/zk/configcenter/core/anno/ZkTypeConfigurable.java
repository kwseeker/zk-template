package top.kwseeker.zk.configcenter.core.anno;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ZkTypeConfigurable {

    /**
     * ZK上面的节点路径
     * 缺省按照以下规则：
     * config/center/default/[applicationName]/[className]/
     */
    String nodePath() default "";

    /**
     * 是否使用指定的zkServers, 默认使用全局的
     */
    boolean useOwnServers() default false;

    /**
     * 指定的zkServers
     */
    String servers() default "";
}
