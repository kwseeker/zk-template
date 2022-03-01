package top.kwseeker.zk.configcenter.core.anno;

import top.kwseeker.zk.configcenter.core.resover.Resolver;
import top.kwseeker.zk.configcenter.core.resover.DefaultResolver;

import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZkFieldConfigurable {

    /**
     * 该配置在zk上面的节点路径（node path）
     * 缺省按照一下规则：
     * config/center/default/applicationName/className/[field]
     */
    String nodePath() default "";

    /**
     * 是否自动更新配置，默认是true
     */
    boolean update() default true;

    /**
     * 用于指定当前类型的类型处理器
     */
    Class<? extends Resolver> resolver() default DefaultResolver.class;
}
