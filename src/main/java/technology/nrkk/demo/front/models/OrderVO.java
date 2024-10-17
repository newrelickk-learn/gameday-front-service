package technology.nrkk.demo.front.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import technology.nrkk.demo.front.entities.Cart;
import technology.nrkk.demo.front.entities.Orders;
import technology.nrkk.demo.front.entities.User;

import java.math.BigDecimal;
import java.util.Set;

@Getter
public class OrderVO {
    private final Integer id;
    private final CartVO cart;

    private final String couponCode;
    private final String paymentType;
    private final String orderStage;
    private final Boolean active;
    private final Boolean purchased;

    public OrderVO(Orders order, CartVO cart) {
        this.id = order.getId();
        this.active = order.getActive();
        this.cart = cart;
        this.couponCode = order.getCouponCode();
        this.paymentType = order.getPaymentType();
        this.orderStage = order.getOrderStage().toString();
        this.purchased = order.getPurchased();
    }

}