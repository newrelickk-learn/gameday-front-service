package technology.nrkk.demo.front.controllers;

import com.newrelic.api.agent.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import technology.nrkk.demo.front.entities.User;
import technology.nrkk.demo.front.services.UserService;

import java.security.Principal;
import java.util.concurrent.ExecutionException;

@Controller
public class UserController {

    @Autowired
    UserService userService;

    @Trace(metricName="/user/new (GET)", dispatcher=true)
    @GetMapping("/user/new")
    public String home(final Model model) {
        model.addAttribute("user", new User());
        return "adduser";
    }

    @Trace(metricName="/user/new (POST)", dispatcher=true)
    @PostMapping("/user/new")
    public Mono<ServerResponse> create(User user) {
        Mono<UserDetails> userDetails = userService.createUser(user.getName(), user.getUsername(), user.getEmail(), user.getPassword());
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userDetails));
    }

    @Trace(metricName="/user (POST)", dispatcher=true)
    @PostMapping("/user")
    public Mono<User> get(Mono<Principal> principal) throws ExecutionException, InterruptedException {
        return principal
                .map(userService::getUserByPrincipal);
    }
}
