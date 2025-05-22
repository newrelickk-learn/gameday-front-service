package technology.nrkk.demo.front.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import technology.nrkk.demo.front.entities.Cart;
import technology.nrkk.demo.front.entities.CartItem;
import technology.nrkk.demo.front.entities.User;
import technology.nrkk.demo.front.models.CartItemVO;
import technology.nrkk.demo.front.models.CartVO;
import technology.nrkk.demo.front.models.Product;
import technology.nrkk.demo.front.repositories.CartRepository;
import technology.nrkk.demo.front.webclient.CatalogueClient;

import java.util.*;
import java.util.concurrent.ExecutionException;

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

    public CartVO getCartVo(Cart cart) {
        List<CartItemVO> list = cart.getItems().stream().map(item -> {
            String productId = item.getProductId();
            try {
                Product product = client.get(productId, cart.getUser());
                return new CartItemVO(item, product);
            } catch (CatalogueClient.CatalogueClientException e) {
                throw new RuntimeException(e);
            }
        }).toList();
        Set<CartItemVO> items = new HashSet<>(list);
        CartVO result = new CartVO(cart);
        result.setItems(items);
        return result;
    }
}
