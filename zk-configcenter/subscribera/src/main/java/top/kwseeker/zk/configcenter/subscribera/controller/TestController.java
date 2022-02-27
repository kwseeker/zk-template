package top.kwseeker.zk.configcenter.subscribera.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/subscriber/a")
@Scope("refresh")
public class TestController {

    @Value("${props.name}")
    private String name;

    @PostMapping("/getProp")
    public String getProp() {
        return name;
    }
}
