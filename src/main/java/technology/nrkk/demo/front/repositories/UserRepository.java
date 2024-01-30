package technology.nrkk.demo.front.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import technology.nrkk.demo.front.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsernameOrEmail(String username, String email);
}