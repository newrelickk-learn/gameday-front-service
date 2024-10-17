package technology.nrkk.demo.front.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import technology.nrkk.demo.front.models.Product;
import technology.nrkk.demo.front.models.Tags;
import technology.nrkk.demo.front.services.UserService;
import technology.nrkk.demo.front.webclient.CatalogueClient;

import java.security.Principal;

@RestController
public class CatalogueController {

    @Autowired
    UserService userService;
    @Autowired
    CatalogueClient client;

    @GetMapping(value={"/catalogue/items"}, produces = "application/json")
    public Mono<Product[]> home(Mono<Principal> principal, @RequestParam String tags) {
        return principal
            .map(userService::getUserByPrincipal)
            .flatMap(user -> client.search(tags));
    }

    @GetMapping(value={"/catalogue/item/{id}/image"}, produces = "application/json")
    public Mono<Product> getImage(Mono<Principal> principal, @PathVariable("id") String id) {
        return principal
                .map(userService::getUserByPrincipal)
                .flatMap(user -> client.get(id));
    }

    @GetMapping(value={"/catalogue/item/{id}"}, produces = "application/json")
    public Mono<Product> getProduct(Mono<Principal> principal, @PathVariable("id") String id) {
        return principal
                .map(userService::getUserByPrincipal)
                .flatMap(user -> client.get(id));
    }

    @GetMapping(value={"/catalogue/tags"}, produces = "application/json")
    public Mono<Tags> getTags(Mono<Principal> principal) {
        return principal
            .map(userService::getUserByPrincipal)
            .flatMap(user -> client.getTags());
    }

}
