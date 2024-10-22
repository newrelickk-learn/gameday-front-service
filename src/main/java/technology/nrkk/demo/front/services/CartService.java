package technology.nrkk.demo.front.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import technology.nrkk.demo.front.entities.Cart;
import technology.nrkk.demo.front.entities.CartItem;
import technology.nrkk.demo.front.entities.User;
import technology.nrkk.demo.front.models.CartItemVO;
import technology.nrkk.demo.front.models.CartVO;
import technology.nrkk.demo.front.repositories.CartRepository;
import technology.nrkk.demo.front.webclient.CatalogueClient;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    CartRepository cartRepo;

    @Autowired
    CatalogueClient client;

    public Cart getCart(User user) {
        Optional<Cart> cartOpt = cartRepo.findByUserAndActive(user, true);
        Assert.state(cartOpt.isPresent(), "Cart must be created before this operation");
        Cart cart = cartOpt.get();
        return cart;
    }

    public Cart getOrCreateCart(User user) {
        Optional<Cart> cartOpt = cartRepo.findByUserAndActive(user, true);
        Cart cart = cartOpt.orElseGet(()-> {
            Cart newCart = new Cart(user);
            cartRepo.save(newCart);
            return newCart;
        });
        return cart;
    }

    public Cart addItem(Cart cart, CartItem item) throws ExecutionException, InterruptedException {
        cart.addItem(item);
        cartRepo.save(cart);
        return cart;
    }

    public Cart inactivate(Cart cart) {
        cart.setActive(false);
        Cart newCart = cartRepo.save(cart);
        return newCart;
    }

    public Mono<CartVO> getCartVo(Cart cart) {
        return Mono.zip(cart.getItems().stream().map(item -> {
            String productId = item.getProductId();
            return client.get(productId, cart.getUser()).map(product ->new CartItemVO(item, product));
        }).collect(Collectors.toList()), (list)->{
                    Set<CartItemVO> items = Arrays.stream(list).map(obj->(CartItemVO) obj).collect(Collectors.toSet());
                    CartVO result = new CartVO(cart);
                    result.setItems(items);
                    return result;
                });
    }
}
