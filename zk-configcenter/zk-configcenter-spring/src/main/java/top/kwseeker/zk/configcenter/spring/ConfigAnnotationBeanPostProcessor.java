package top.kwseeker.zk.configcenter.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.lang.NonNull;
import top.kwseeker.zk.configcenter.core.ZkRegisterFactory;
import top.kwseeker.zk.configcenter.core.anno.ZkTypeConfigurable;

public class ConfigAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter {

    private static String _G_SERVERS = "127.0.0.1:2181";
    private static Boolean _CREATE_WHEN_NULL = Boolean.TRUE;

    public ConfigAnnotationBeanPostProcessor(String severs, Boolean createWhenNull) {
        _G_SERVERS = severs;
        _CREATE_WHEN_NULL = createWhenNull;
    }

    //实例化
    @Override
    public boolean postProcessAfterInstantiation(final Object bean, @NonNull String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        ZkTypeConfigurable cfg = clazz.getAnnotation(ZkTypeConfigurable.class);
        if (cfg != null) {
            ZkRegisterFactory.getZkRegister(_G_SERVERS).register(bean.getClass(), _CREATE_WHEN_NULL);
        }
        return true;
    }

    //初始化
    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        return super.postProcessAfterInitialization(bean, beanName);
    }
}
