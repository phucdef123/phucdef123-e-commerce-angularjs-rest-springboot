package com.assignment.service;

import com.assignment.entity.Order;

import java.util.List;

public interface OrderService {
    List<Order> findAll();
    Order findById(Integer id);
    List<Order> findByUsername(String username);
    Order save(Order order);
    Boolean existsById(Integer id);
    void deleteById(Integer id);
    List<Order> searchOrders(String keyword);
    Boolean canUpdateStatus(Order.OrderStatus current, Order.OrderStatus target);
}
