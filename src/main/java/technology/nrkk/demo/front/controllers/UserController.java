package technology.nrkk.demo.front.controllers;

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

@Controller
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/user/new")
    public String home(final Model model) {
        model.addAttribute("user", new User());
        return "adduser";
    }

    @PostMapping("/user/new")
    public Mono<ServerResponse> create(User user) {
        Mono<UserDetails> userDetails = userService.createUser(user.getName(), user.getUsername(), user.getEmail(), user.getPassword());
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(userDetails));
    }

}
