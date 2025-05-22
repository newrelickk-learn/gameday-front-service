package technology.nrkk.demo.front.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import technology.nrkk.demo.front.entities.Cart;
import technology.nrkk.demo.front.entities.Orders;
import technology.nrkk.demo.front.models.Product;
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

    public Orders assertStock(Optional<Orders> optOrder) {
        Assert.state(optOrder.isPresent(), String.format("Order must present"));
        Orders order = optOrder.get();
        Assert.state(order.getActive(), "Order must be activated");
        Assert.state(order.getOrderStage().equals(Orders.OrderStage.CONFIRM), "Order status must be CONFIRM");
        boolean stockValid = order.getCart().getItems()
                .stream().map(item -> {
                    try {
                        Product product = catalogueService.get(item.getProductId(), order.getUser());
                        return product.getCount() > item.getAmount();
                    } catch (CatalogueClient.CatalogueClientException e) {
                        throw new RuntimeException(e);
                    }
                }).reduce(true, (result, val) -> result && val);
        if (!stockValid) {
            throw new RuntimeException("There are no stock");
        }
        return order;
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
