package technology.nrkk.demo.front.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Orders {

    public enum OrderStage {
        NEW("NEW"), CONFIRM("CONFIRM"), PURCHASED("PURCHASED"), SHIPPED("SHIPPED"), DELIVERED("DELIVERED");
        private final String text;
        private OrderStage(final String text) {
            this.text = text;
        }
        public String getString() {
            return this.text;
        }

        public boolean equals(OrderStage stage) {
            return this.text.equals(stage.getString());
        }
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    private String couponCode;
    private String paymentType;
    private String orderStage;
    private Boolean active;
    private Boolean purchased;

    public Orders(Cart cart) {
        this.orderStage = OrderStage.NEW.getString();
        this.purchased = false;
        this.active = true;
        this.cart = cart;
        this.user = cart.getUser();
    }

    public void setOrderStage(OrderStage orderStage) {
        this.orderStage = orderStage.getString();
    }

    public OrderStage getOrderStage() {
        return OrderStage.valueOf(this.orderStage);
    }

    public Orders() {
        this.orderStage = OrderStage.NEW.getString();
        this.purchased = false;
        this.active = true;
    }
}