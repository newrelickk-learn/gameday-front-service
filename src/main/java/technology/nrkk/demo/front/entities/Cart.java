package technology.nrkk.demo.front.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Entity
@Data
public class Cart implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<CartItem> items;

    public Cart(Set<CartItem> items) {
        this.active = true;
        this.items = items;
    }

    public void addItem(CartItem item) {
        Optional<CartItem> storedItem = this.items.stream().filter(i->i.equals(item)).findFirst();
        if (storedItem.isEmpty()) {
            this.items.add(item);
        } else {
            storedItem.get().setAmount(storedItem.get().getAmount()+1);
        }
    }
    public Cart(User user) {
        this.user = user;
        this.active = true;
        this.items = new HashSet<>();
    }

    public Cart() {
    }
}