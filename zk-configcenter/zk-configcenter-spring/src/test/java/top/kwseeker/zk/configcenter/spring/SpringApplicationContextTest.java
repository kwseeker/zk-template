package top.kwseeker.zk.configcenter.spring;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringApplicationContextTest {

    private ApplicationContext applicationContext;

    @Before
    public void beforeClass() {
        applicationContext = new ClassPathXmlApplicationContext("spring.xml");
    }

    /**
     * 这个测试相当于模拟配置切换
     */
    @Test
    public void cycleReadConfig() throws InterruptedException {
        Config config = applicationContext.getBean("bizConfig", BizConfig.class);
        while(true) {
            config.print();
            Thread.sleep(2000L);
        }
    }
}
