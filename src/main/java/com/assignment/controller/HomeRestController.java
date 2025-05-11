package com.assignment.controller;

import com.assignment.entity.Category;
import com.assignment.entity.Order;
import com.assignment.entity.OrderDetail;
import com.assignment.entity.Product;
import com.assignment.service.CategoryService;
import com.assignment.service.OrderDetailService;
import com.assignment.service.OrderService;
import com.assignment.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/home/index")
public class HomeRestController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;
    
    @GetMapping("/products/category/{id}")
    public ResponseEntity<List<Product>> getProductsByCategoryId(@PathVariable Integer id) {
        if (id == null || id <= 0 ) {
            return ResponseEntity.badRequest().build();
        }
        if (!categoryService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productService.findByCategory(id));
    }

    @GetMapping("/products/newest")
    public ResponseEntity<List<Product>> getProductsNewest() {
        return ResponseEntity.ok(productService.listTop10Newest());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategory() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/products/mostpurchased")
    public ResponseEntity<List<Product>> getProductsMostPurchased() {
        Pageable pageable = PageRequest.of(0, 10);
        return ResponseEntity.ok(productService.listMostPurchasedProducts(pageable));
    }

    @GetMapping("/shop")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/shop/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer id) {
        if (id == null || id <= 0 ) {
            return ResponseEntity.badRequest().build();
        }
        if (!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping("/order/save")
    public ResponseEntity<Order> postOrder(@RequestBody Order order) {
        if (order == null || order.getUser().getUsername() == null || order.getUser().getUsername().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (orderService.existsById(order.getId())) {
            return ResponseEntity.badRequest().build();
        }
        order.setId(null);
        return ResponseEntity.ok(orderService.save(order));
    }

    @PostMapping("/orderdetail/save")
    public ResponseEntity<OrderDetail> postOrderdetail(@RequestBody OrderDetail orderDetail) {
        if (orderDetail == null || orderDetail.getOrder().getId() == null ||
                orderDetail.getOrder().getId() <= 0 || orderDetail.getProduct().getId() == null ||
                orderDetail.getProduct().getId() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        if (orderDetailService.existsById(orderDetail.getId())) {
            return ResponseEntity.badRequest().build();
        }
        orderDetail.setId(null);
        return ResponseEntity.ok(orderDetailService.save(orderDetail));
    }
}
