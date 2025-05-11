package com.assignment.controller;

import com.assignment.entity.Order;
import com.assignment.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/orders")
public class OrderRestController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<List<Order>> searchOrders(@RequestParam(required = false) String keyword) {
        List<Order> orders = orderService.searchOrders(keyword);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> get(@PathVariable("id") Integer id) {
        if (id == null || id <= 0 ) {
            return ResponseEntity.badRequest().build();
        }
        if (!orderService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderService.findById(id));
    }

//    @GetMapping("/order-statuses")
//    public ResponseEntity<Order.OrderStatus[]> getStatuses() {
//        return ResponseEntity.ok(Order.OrderStatus.values());
//    }

    @PostMapping
    public ResponseEntity<Order> post(@RequestBody Order order) {
        if (order == null) {
            return ResponseEntity.badRequest().build();
        }
        if (order.getId() != null && orderService.existsById(order.getId())) {
            return ResponseEntity.notFound().build();
        }
        order.setId(null);
        return ResponseEntity.ok(orderService.save(order));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> put(@RequestBody Order order, @PathVariable("id") Integer id) {
        if (order.getId() == null || id == null || id <= 0 ) {
            return ResponseEntity.badRequest().build();
        }
        if (!orderService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Order existingOrder = orderService.findById(id);
        Order.OrderStatus currentStatus = existingOrder.getStatus(); // Lấy trạng thái hiện tại
        Order.OrderStatus newStatus = order.getStatus(); // Trạng thái request muốn chuyển

        if (!orderService.canUpdateStatus(currentStatus, newStatus)) {
            System.out.println("Không thể cập nhật trạng thái từ " + currentStatus + " sang " + newStatus);
            return ResponseEntity.badRequest().build(); // nên trả 400 thay vì 404
        }

        // Cập nhật các field khác nếu cần, ví dụ địa chỉ, user...
        existingOrder.setStatus(newStatus);
        return ResponseEntity.ok(orderService.save(existingOrder));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        if (id == null || id <= 0 ) {
            return ResponseEntity.badRequest().build();
        }
        if (!orderService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

