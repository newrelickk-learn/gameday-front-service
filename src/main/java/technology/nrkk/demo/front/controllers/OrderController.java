package technology.nrkk.demo.front.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import technology.nrkk.demo.front.entities.Cart;
import technology.nrkk.demo.front.entities.Orders;
import technology.nrkk.demo.front.entities.User;
import technology.nrkk.demo.front.models.CartVO;
import technology.nrkk.demo.front.models.OrderVO;
import technology.nrkk.demo.front.services.CartService;
import technology.nrkk.demo.front.services.OrdersService;
import technology.nrkk.demo.front.services.UserService;
import org.springframework.http.*;
import technology.nrkk.demo.front.webclient.PaymentClient;

import java.security.Principal;
import java.util.List;

@RestController
public class OrderController {

    @Autowired
    UserService userService;
    @Autowired
    CartService cartService;
    @Autowired
    OrdersService orderService;
    @Autowired
    private PaymentClient paymentClient;

    @PostMapping(value = "/order")
    public ResponseEntity<OrderVO> get(Principal principal, final Model model) {
        try {
            User user = userService.getUserByPrincipal(principal);
            Cart cart = cartService.getOrCreateCart(user);
            Orders order = orderService.getOrCreateOrder(cart);
            CartVO cartVO = cartService.getCartVo(order.getCart());
            OrderVO orderVO = new OrderVO(order, cartVO);
            model.addAttribute("order", orderVO);
            return ResponseEntity.ok(orderVO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping(value = "/order")
    public ResponseEntity<OrderVO> startOrder(Principal principal, final Model model) {
        try {
            User user = userService.getUserByPrincipal(principal);
            Cart cart = cartService.getOrCreateCart(user);
            Orders order = orderService.getOrCreateOrder(cart);
            CartVO cartVO = cartService.getCartVo(order.getCart());
            OrderVO orderVO = new OrderVO(order, cartVO);
            model.addAttribute("order", orderVO);
            return ResponseEntity.ok(orderVO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping(value = "/order/confirm", produces = "application/json")
    public ResponseEntity<OrderVO> setConfirmFromNew(Principal principal, @RequestBody Orders reqOrder, final Model model) {
        try {
            User user = userService.getUserByPrincipal(principal);
            Cart cart = cartService.getCart(user);
            Orders order = orderService.getOrder(cart, Orders.OrderStage.NEW);
            order.setCouponCode(reqOrder.getCouponCode());
            order.setPaymentType(reqOrder.getPaymentType());
            Orders newOrder = orderService.setStatusConfirmFromNew(order);
            CartVO cartVO = cartService.getCartVo(newOrder.getCart());
            OrderVO orderVO = new OrderVO(order, cartVO);
            model.addAttribute("order", orderVO);
            return ResponseEntity.ok(orderVO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping(value = "/order/confirm", produces = "application/json")
    public ResponseEntity<OrderVO> getConfirm(Principal principal, final Model model) {
        try {
            User user = userService.getUserByPrincipal(principal);
            Cart cart = cartService.getCart(user);
            Orders order = orderService.getOrder(cart, Orders.OrderStage.CONFIRM);
            CartVO cartVO = cartService.getCartVo(order.getCart());
            OrderVO orderVO = new OrderVO(order, cartVO);
            model.addAttribute("order", orderVO);
            return ResponseEntity.ok(orderVO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping(value = "/order/purchase", produces = "application/json")
    public ResponseEntity<OrderVO> setPurchaseFromConfirm(Principal principal, final Model model) {
        User user = userService.getUserByPrincipal(principal);
        Cart cart = cartService.getCart(user);
        Orders order = orderService.getOrder(cart, Orders.OrderStage.CONFIRM);
        CartVO cartVO = cartService.getCartVo(cart);

        Integer amount = cartVO.getTotalPrice().intValue();
        Integer customerId = user.getId();
        String cardId = order.getPaymentType();

        try {
            paymentClient.pay(amount, customerId, cardId, "success");
            Orders newOrder = orderService.setStatusPurchaseFromConfirm(order);
            CartVO newCartVO = cartService.getCartVo(newOrder.getCart());
            OrderVO orderVO = new OrderVO(newOrder, newCartVO);
            model.addAttribute("order", orderVO);
            return ResponseEntity.ok(orderVO);
        } catch (PaymentClient.PaymentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping(value = "/order/{id}/ship", produces = "application/json")
    public ResponseEntity<OrderVO> setShippedFromPurchased(Principal principal, @PathVariable("id") Integer id) {
        try {
            User user = userService.getUserByPrincipal(principal);
            Orders order = orderService.assertStock(orderService.getOrderById(id));
            if (order.getUser().getId().equals(user.getId())) {
                Orders newOrder = orderService.setStatusShippedFromPurchased(order);
                CartVO cartVO = cartService.getCartVo(newOrder.getCart());
                return ResponseEntity.ok(new OrderVO(newOrder, cartVO));
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping(value = "/order/purchase", produces = "application/json")
    public ResponseEntity<OrderVO> getPurchase(Principal principal, final Model model) {
        try {
            User user = userService.getUserByPrincipal(principal);
            Cart cart = cartService.getCart(user);
            Orders order = orderService.getOrder(cart, Orders.OrderStage.PURCHASED);
            CartVO cartVO = cartService.getCartVo(order.getCart());
            OrderVO orderVO = new OrderVO(order, cartVO);
            model.addAttribute("order", orderVO);
            return ResponseEntity.ok(orderVO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping(value = "/admin/order/list", produces = "application/json")
    public List<OrderVO> getOrders(Principal principal, final Model model) {
        // Just Check User exist.
        userService.getUserByPrincipal(principal);
        return orderService.getAll().stream().map(order -> new OrderVO(order, null)).toList();
    }
}
