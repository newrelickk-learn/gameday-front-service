package technology.nrkk.demo.front.controllers.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginHeadController {
    @RequestMapping(value = "/login", method = RequestMethod.HEAD)
    public ResponseEntity<Void> loginHead() {
        return ResponseEntity.ok().build();
    }
}






