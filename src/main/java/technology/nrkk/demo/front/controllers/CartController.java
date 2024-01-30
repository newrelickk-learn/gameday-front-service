package technology.nrkk.demo.front.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import technology.nrkk.demo.front.entities.Cart;
import technology.nrkk.demo.front.entities.CartItem;
import technology.nrkk.demo.front.models.CartVO;
import technology.nrkk.demo.front.services.CartService;
import technology.nrkk.demo.front.services.UserService;

import java.security.Principal;
import java.util.concurrent.ExecutionException;

@RestController
public class CartController {

    @Autowired
    UserService userService;
    @Autowired
    CartService cartService;

    @PostMapping(value = "/cart/add", produces = "application/json")
    public Mono<Cart> addItem(Mono<Principal> principal, @RequestBody CartItem cartItem) {
        return principal
            .map(userService::getUserByPrincipal)
            .map(cartService::getOrCreateCart)
            .handle((cart, sink) -> {
                try {
                    sink.next(cartService.addItem(cart, cartItem));
                } catch (ExecutionException e) {
                    sink.error(new RuntimeException(e));
                } catch (InterruptedException e) {
                    sink.error(new RuntimeException(e));
                }
            });
    }

    @GetMapping(value = "/cart", produces = "application/json")
    public Mono<CartVO> get(Mono<Principal> principal) throws ExecutionException, InterruptedException {
        return principal
            .map(userService::getUserByPrincipal)
            .map(cartService::getOrCreateCart)
            .flatMap(cartService::getCartVo);
    }



}
