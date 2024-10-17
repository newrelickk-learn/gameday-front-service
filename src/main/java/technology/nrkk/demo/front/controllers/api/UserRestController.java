package technology.nrkk.demo.front.controllers.api;

import com.newrelic.api.agent.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import technology.nrkk.demo.front.entities.User;
import technology.nrkk.demo.front.models.UserVO;
import technology.nrkk.demo.front.services.UserService;

import java.security.Principal;
import java.util.concurrent.ExecutionException;

@RestController
public class UserRestController {

    @Autowired
    UserService userService;

    @Trace(metricName="/api/user (POST)", dispatcher=true)
    @PostMapping("/api/user")
    public Mono<UserVO> get(Mono<Principal> principal) throws ExecutionException, InterruptedException {
        return principal
                .map(userService::getUserByPrincipal)
                .map(UserVO::new);
    }
}
