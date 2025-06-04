package technology.nrkk.demo.front.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    protected final static Logger logger = LoggerFactory.getLogger(IndexController.class);
    @GetMapping(value={"", "/", "/product/{id}", "/admin", "/admin/"})
    public String index() {
        logger.info("index page loaded");
        return "index";
    }
}
