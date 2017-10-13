package io.stubmarine;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SinglePageController {
    @RequestMapping({"/inbox/*"})
    public String redirect() {
        return "forward:/";
    }
}
