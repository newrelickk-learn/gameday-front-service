package technology.nrkk.demo.front.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping(value={"", "/", "/admin", "/admin/"})
    public String index() {
        return "index";
    }
}
