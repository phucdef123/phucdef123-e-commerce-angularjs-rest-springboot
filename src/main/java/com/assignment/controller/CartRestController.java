package com.assignment.controller;

import com.assignment.entity.Cart;
import com.assignment.entity.Order;
import com.assignment.entity.OrderDetail;
import com.assignment.service.CartService;
import com.assignment.service.OrderDetailService;
import com.assignment.service.OrderService;
import com.assignment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartRestController {
    @Autowired
    CartService cartService;

    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderDetailService orderDetailService;

    @GetMapping
    public ResponseEntity<List<Cart>> getAll() {
        return ResponseEntity.ok(cartService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cart> getById(@PathVariable Integer id) {
        if (id == null || id <= 0 ) {
            return ResponseEntity.badRequest().build();
        }
        if (!cartService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cartService.findById(id));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Cart>> getByUsername(@PathVariable String username) {
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (!userService.existsByUsername(username)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cartService.findByUsername(username));
    }

    @PostMapping
    public ResponseEntity<Cart> post(@RequestBody Cart cart) {
        if (cart.getProduct() == null || cart.getUser() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (cart.getId() != null && cartService.existsById(cart.getId())) {
            return ResponseEntity.notFound().build();
        }
        cart.setId(null);
        return ResponseEntity.ok(cartService.save(cart));
    }

    @PostMapping("/order/{username}")
    public ResponseEntity<Order> postOrder(@RequestBody Order order, @PathVariable String username) {
        if (order.getUser() == null || order.getUser().getUsername() == null ||
                order.getUser().getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (!order.getUser().getUsername().equals(username)) {
            return ResponseEntity.badRequest().body(null); // Username không khớp
        }
        if (!userService.existsByUsername(username)) {
            return ResponseEntity.notFound().build();
        }
        try {
            Order savedOrder = orderService.save(order);

            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/order/orderDetails")
    public ResponseEntity<OrderDetail> postOrderDetails(@RequestBody OrderDetail orderDetail) {
        if (orderDetail == null || orderDetail.getProduct() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (orderDetail.getOrder() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(orderDetailService.save(orderDetail));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cart> put(@PathVariable("id") Integer id, @RequestBody Cart cart) {
        if (cart.getId() == null || cart.getProduct() == null || cart.getUser() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!cartService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        cart.setId(id);
        return ResponseEntity.ok(cartService.save(cart));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        if (id == null || id <= 0 ) {
            return ResponseEntity.badRequest().build();
        }
        if (!cartService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        cartService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
