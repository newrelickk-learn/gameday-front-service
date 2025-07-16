package technology.nrkk.demo.front.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import technology.nrkk.demo.front.entities.Cart;
import technology.nrkk.demo.front.entities.Orders;

import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    Optional<Orders> findByCartAndPurchasedAndActive(Cart cart, Boolean purchased, Boolean active);
    Optional<Orders> findByCartAndOrderStageAndActive(Cart cart, String orderStage, Boolean active);
    Optional<Orders> findByCart(Cart cart);

}