package top.kwseeker.zkboot.zkbootbusiservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.kwseeker.zk.common.service.GreetService;

@RestController
public class TestController {

    @Autowired
    private GreetService greetService;

    @GetMapping("/greet")
    public String greet() {
        return greetService.sayHello("Arvin Lee");
    }
}
