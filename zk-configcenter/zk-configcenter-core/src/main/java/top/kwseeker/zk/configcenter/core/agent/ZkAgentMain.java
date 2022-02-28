package top.kwseeker.zk.configcenter.core.agent;

import lombok.extern.slf4j.Slf4j;
import top.kwseeker.zk.configcenter.core.Constant;
import top.kwseeker.zk.configcenter.core.ZkRegister;
import top.kwseeker.zk.configcenter.core.ZkRegisterFactory;
import top.kwseeker.zk.configcenter.core.anno.ZkTypeConfigurable;

import java.lang.instrument.Instrumentation;

/**
 * 启动前，扫描所有加载的类，获取其中ZkTypeConfigurable注解的类，进行注册
 */
@Slf4j
public class ZkAgentMain extends Agent {

    /**
     * 这里只是用到premain方法在main之前执行, 只是提前做些初始化工作，并没有做字节码增强什么的
     */
    public static void premain(String agentArgs, final Instrumentation inst) {
        String servers = System.getProperty(Constant.ZK, ZkRegister.DEFAULT_SERVERS).trim();
        if (servers.isEmpty()) {
            return;
        }

        final ZkRegister register = ZkRegisterFactory.getZkRegister(servers);
        new Thread(() -> {
            try {
                Thread.sleep(10000L);   //默认10s后启动解析器
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            resolve(inst, register);
        }).start();
    }

    public static void resolve(Instrumentation inst, ZkRegister register) {
        log.debug("resolve: register class handler ...");
        try {
            for (Class<?> clazz : inst.getAllLoadedClasses()) {
                ZkTypeConfigurable typeConfigurable = clazz.getAnnotation(ZkTypeConfigurable.class);
                if (null != typeConfigurable) {
                    register.register(clazz, true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("resolve error!", e);
        }
    }
}
