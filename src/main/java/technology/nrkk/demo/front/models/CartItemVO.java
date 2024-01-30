package technology.nrkk.demo.front.models;

import lombok.Getter;
import technology.nrkk.demo.front.entities.CartItem;

@Getter
public class CartItemVO {
    private Integer id;
    private Integer amount;
    private Product product;

    public CartItemVO(CartItem cartItem, Product product) {
        this.id = cartItem.getId();
        this.amount = cartItem.getAmount();
        this.product = product;
    }

}