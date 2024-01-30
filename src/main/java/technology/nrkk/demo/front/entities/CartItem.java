package technology.nrkk.demo.front.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    private String productId;

    private Integer amount;

    public CartItem(Cart cart, String productId, Integer amount) {
        this.cart = cart;
        this.productId = productId;
        this.amount = amount;
    }

    public CartItem() {

    }

    public boolean equals(CartItem item) {
        return this.productId.equals(item.productId);
    }

    public int hashCode(CartItem item) {
        return item.productId.hashCode();
    }
}