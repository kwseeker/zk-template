package top.kwseeker.zk.configcenter.core.agent;

import top.kwseeker.zk.configcenter.core.ZkRegister;
import top.kwseeker.zk.configcenter.core.ZkRegisterFactory;

public abstract class Agent {

    protected static void agent(final String servers, final String clas) throws Exception {
        if (null == servers || servers.isEmpty()) {
            return;
        }
        final String[] classes = clas.split(",");
        if (classes.length == 0) {
            return;
        }
        final ZkRegister register = ZkRegisterFactory.getZkRegister(servers);
        for (String clzStr : classes) {
            Class clazz = getClass(clzStr, 2);//循环取三次
            if (clazz != null) {
                register.register(clazz, true);
            }
        }
    }

    private static Class getClass(String clzStr, int time) {
        try {
            if (time < 1) return null;
            return Class.forName(clzStr);
        } catch (ClassNotFoundException e) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return getClass(clzStr, time--);
        }
    }
}
