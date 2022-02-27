package top.kwseeker.zk.configcenter.publisher.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "zk.configcenter")
public class ZkConfigCenterProperties {

    private String znodeBase;
    private Map<String, List<String>> subscriberPropFiles;
    private String globalPropFile;
}
