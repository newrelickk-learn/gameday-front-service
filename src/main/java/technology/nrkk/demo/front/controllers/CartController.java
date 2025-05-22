package technology.nrkk.demo.front.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import technology.nrkk.demo.front.entities.Cart;
import technology.nrkk.demo.front.entities.CartItem;
import technology.nrkk.demo.front.entities.User;
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
    public Cart addItem(Principal principal, @RequestBody CartItem cartItem) throws ExecutionException, InterruptedException {
        User user = userService.getUserByPrincipal(principal);
        Cart cart = cartService.getOrCreateCart(user);
        return cartService.addItem(cart, cartItem);
    }

    @GetMapping(value = "/cart", produces = "application/json")
    public CartVO get(Principal principal) throws ExecutionException, InterruptedException {
        User user = userService.getUserByPrincipal(principal);
        Cart cart = cartService.getOrCreateCart(user);
        return cartService.getCartVo(cart);
    }



}
