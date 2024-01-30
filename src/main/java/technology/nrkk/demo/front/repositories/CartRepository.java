package technology.nrkk.demo.front.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import technology.nrkk.demo.front.entities.Cart;
import technology.nrkk.demo.front.entities.User;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUserAndActive(User user, Boolean active);
}