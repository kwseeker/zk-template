package top.kwseeker.zk.configcenter.subscribera.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "zk.configcenter")
public class ZkConfigNodes {

    private List<String> configNodes;
}
