package technology.nrkk.demo.front.models;

import jakarta.persistence.Column;
import lombok.Getter;
import technology.nrkk.demo.front.entities.Cart;
import technology.nrkk.demo.front.entities.User;

import java.math.BigDecimal;
import java.util.Set;

@Getter
public class UserVO {
    private Integer id;
    private String name;
    private String username;
    private String email;
    private String companyId;

    public UserVO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.companyId = user.getCompanyId();
    }


}