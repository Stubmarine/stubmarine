package io.wallraff;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SinglePageController {
    @RequestMapping({"/inbox/*", "/endpoint/*"})
    public String redirect() {
        return "forward:/";
    }
}
