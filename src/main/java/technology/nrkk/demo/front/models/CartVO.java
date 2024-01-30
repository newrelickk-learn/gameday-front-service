package technology.nrkk.demo.front.models;

import lombok.Getter;
import technology.nrkk.demo.front.entities.Cart;

import java.math.BigDecimal;
import java.util.Set;

@Getter
public class CartVO {
    private Integer id;
    private Boolean active;
    private BigDecimal totalPrice;
    private Integer amount;
    private Set<CartItemVO> items;

    public CartVO(Cart cart) {
        this.id = cart.getId();
        this.active = cart.getActive();
    }

    public void setItems(Set<CartItemVO> items) {
        this.items = items;
        this.totalPrice = BigDecimal.valueOf(items.stream().map(item -> item.getAmount().intValue() * item.getProduct().getPrice().doubleValue()).reduce(0.0, (current, price) -> current + price));
        this.amount = items.stream().map(item -> item.getAmount().intValue()).reduce(0, (current, amount) -> current + amount);
    }

}