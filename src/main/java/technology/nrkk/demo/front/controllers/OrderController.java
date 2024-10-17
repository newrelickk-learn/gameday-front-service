package technology.nrkk.demo.front.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import technology.nrkk.demo.front.entities.Cart;
import technology.nrkk.demo.front.entities.Orders;
import technology.nrkk.demo.front.models.OrderVO;
import technology.nrkk.demo.front.services.CartService;
import technology.nrkk.demo.front.services.OrdersService;
import technology.nrkk.demo.front.services.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
public class OrderController {

    @Autowired
    UserService userService;
    @Autowired
    CartService cartService;
    @Autowired
    OrdersService orderService;

    @PostMapping(value = "/order")
    public Mono<OrderVO> get(Mono<Principal> principal, final Model model) {
        return principal
                .map(userService::getUserByPrincipal)
                .map(cartService::getCart)
                .map(orderService::getOrCreateOrder)
                .map(order -> cartService.getCartVo(order.getCart())
                        .map(cartVO -> new OrderVO(order, cartVO)))
                .flatMap(order -> {
                    model.addAttribute("order", order);
                    return order;
                });
    }

    @GetMapping(value = "/order")
    public Mono<OrderVO> startOrder(Mono<Principal> principal, final Model model) {
        return principal
                .map(userService::getUserByPrincipal)
                .map(cartService::getCart)
                .map(orderService::getOrCreateOrder)
                .map(order -> cartService.getCartVo(order.getCart())
                        .map(cartVO -> new OrderVO(order, cartVO)))
                .flatMap(order -> order)
                .map(order -> {
                    model.addAttribute("order", order);
                    return order;
                });
    }

    @PostMapping(value = "/order/confirm", produces = "application/json")
    public Mono<OrderVO> setConfirmFromNew(Mono<Principal> principal, @RequestBody Orders reqOrder, final Model model) {
        return principal
                .map(userService::getUserByPrincipal)
                .map(cartService::getCart)
                .map(cart -> orderService.getOrder(cart, Orders.OrderStage.NEW))
                .map(order -> {
                    order.setCouponCode(reqOrder.getCouponCode());
                    order.setPaymentType(reqOrder.getPaymentType());
                    return order;
                })
                .map(orderService::setStatusConfirmFromNew)
                .map(order -> cartService.getCartVo(order.getCart())
                        .map(cartVO -> new OrderVO(order, cartVO)))
                .flatMap(order -> {
                    model.addAttribute("order", order);
                    return order;
                });
    }

    @GetMapping(value = "/order/confirm", produces = "application/json")
    public Mono<OrderVO> getConfirm(Mono<Principal> principal, final Model model) {
        return principal
                .map(userService::getUserByPrincipal)
                .map(cartService::getCart)
                .map(cart -> orderService.getOrder(cart, Orders.OrderStage.CONFIRM))
                .map(order -> cartService.getCartVo(order.getCart())
                        .map(cartVO -> new OrderVO(order, cartVO)))
                .flatMap(order -> order)
                .map(order -> {
                    model.addAttribute("order", order);
                    return order;
                });
    }

    @PostMapping(value = "/order/purchase", produces = "application/json")
    public Mono<OrderVO> setPurchaseFromConfirm(Mono<Principal> principal, final Model model) {
        return principal
                .map(userService::getUserByPrincipal)
                .map(cartService::getCart)
                .map(cart -> {
                    Cart newCart = cartService.inactivate(cart);
                    return orderService.getOrder(newCart, Orders.OrderStage.CONFIRM);
                })
                .map(orderService::setStatusPurchaseFromConfirm)
                .map(order -> cartService.getCartVo(order.getCart())
                        .map(cartVO -> new OrderVO(order, cartVO)))
                .flatMap(order -> order)
                .map(order -> {
                    model.addAttribute("order", order);
                    return order;
                });
    }

    @PostMapping(value = "/order/{id}/ship", produces = "application/json")
    public Mono<OrderVO> setShippedFromPurchased(Mono<Principal> principal, @PathVariable("id") Integer id) {
        return principal
                .map(userService::getUserByPrincipal)
                .map(user -> orderService.getOrderById(id))
                .flatMap(orderService::assertStock)
                .map(orderService::setStatusShippedFromPurchased)
                .map(order -> cartService.getCartVo(order.getCart())
                        .map(cartVO -> new OrderVO(order, cartVO)))
                .flatMap(order -> order);
    }

    @GetMapping(value = "/order/purchase", produces = "application/json")
    public Mono<OrderVO> getPurchase(Mono<Principal> principal, final Model model) {
        return principal
                .map(userService::getUserByPrincipal)
                .map(cartService::getCart)
                .map(cart -> orderService.getOrder(cart, Orders.OrderStage.PURCHASED))
                .map(order -> cartService.getCartVo(order.getCart())
                        .map(cartVO -> new OrderVO(order, cartVO)))
                .flatMap(order -> order)
                .map(order -> {
                    model.addAttribute("order", order);
                    return order;
                });
    }

    @GetMapping(value = "/admin/order/list", produces = "application/json")
    public Mono<List<OrderVO>> getOrders(Mono<Principal> principal, final Model model) {
        return principal
                .map(userService::getUserByPrincipal)
                .map((user) -> orderService.getAll())
                .map(orderList -> orderList.stream().map(order -> new OrderVO(order, null)).toList());
    }
}
