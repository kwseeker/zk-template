package top.kwseeker.zkboot.zkbootdiscovery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.kwseeker.zkboot.zkbootdiscovery.util.ZKConnectionCache;

@Configuration
public class ApplicationConfig {

    //连接缓冲池
    @Bean
    public ZKConnectionCache createCache() {
        return new ZKConnectionCache();
    }
}
