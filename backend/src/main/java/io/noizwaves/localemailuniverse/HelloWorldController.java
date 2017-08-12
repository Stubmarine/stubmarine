package io.noizwaves.localemailuniverse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloWorldController {
    
    @RequestMapping("/api/helloWorld")
    public Map<String, String> getHelloWorld() {
        HashMap<String, String> map = new HashMap<>();
        map.put("hello", "world");
        return map;
    }
}
