package technology.nrkk.demo.front.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technology.nrkk.demo.front.entities.Cart;
import technology.nrkk.demo.front.entities.Orders;
import technology.nrkk.demo.front.repositories.CartRepository;
import technology.nrkk.demo.front.repositories.OrdersRepository;
import technology.nrkk.demo.front.webclient.CatalogueClient;

import java.util.List;
import java.util.Optional;

@Service
public class OrdersService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    CatalogueClient client;

    @Autowired
    CatalogueService catalogueService;

    public Orders getOrCreateOrder(Cart cart) {
        Orders order = ordersRepository.findByCartAndPurchasedAndActive(cart, false,true).orElseGet(()-> {
            Orders newOrder = new Orders(cart);
            return ordersRepository.save(newOrder);
        });
        if (order.getCart() == null) {
            order.setCart(cart);
        }
        return order;
    }

    public Orders setStatusConfirmFromNew(Orders order) {
        Assert.state(order.getActive(), "Order must be activated");
        Assert.state(order.getOrderStage().equals(Orders.OrderStage.NEW), "Order status must be NEW");
        order.setOrderStage(Orders.OrderStage.CONFIRM);
        Orders newOrder = ordersRepository.save(order);
        return newOrder;
    }

    public Mono<Orders> assertStock(Optional<Orders> optOrder) {
        Assert.state(optOrder.isPresent(), String.format("Order must present"));
        Orders order = optOrder.get();
        Assert.state(order.getActive(), "Order must be activated");
        Assert.state(order.getOrderStage().equals(Orders.OrderStage.CONFIRM), "Order status must be CONFIRM");
        return Flux.fromIterable(order.getCart().getItems())
            .flatMap(item -> catalogueService.get(item.getProductId(), order.getUser()).map(product -> product.getCount() > item.getAmount()))
            .all(res -> res)
            .handle((res, sink) -> {
                if (res) {
                    sink.next(order);
                } else {
                    sink.error(new RuntimeException("There are no stock"));
            }});
    }

    public Orders setStatusPurchaseFromConfirm(Orders order) {
        Assert.state(order.getActive(), "Order must be activated");
        Assert.state(order.getOrderStage().equals(Orders.OrderStage.CONFIRM), "Order status must be CONFIRM");
        order.setOrderStage(Orders.OrderStage.PURCHASED);
        order.setPurchased(true);
        Orders newOrder = ordersRepository.save(order);
        return newOrder;
    }

    public Orders setStatusShippedFromPurchased(Orders order) {
        Assert.state(order.getActive(), "Order must be activated");
        Assert.state(order.getOrderStage().equals(Orders.OrderStage.PURCHASED), "Order status must be PURCHASED");
        order.setOrderStage(Orders.OrderStage.SHIPPED);
        order.setPurchased(true);
        Orders newOrder = ordersRepository.save(order);
        return newOrder;
    }

    public Orders getOrder(Cart cart, Orders.OrderStage stage) {
        Optional<Orders> order = ordersRepository.findByCartAndOrderStageAndActive(cart, stage.getString(), true);
        return order.get();
    }

    public Optional<Orders> getOrderById(Integer id) {
        Optional<Orders> order = ordersRepository.findById(id);
        return order;
    }

    public List<Orders> getAll() {
        List<Orders> orders = ordersRepository.findAll();
        return orders;
    }
}
