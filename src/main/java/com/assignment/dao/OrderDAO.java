package com.assignment.dao;

import com.assignment.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDAO extends JpaRepository<Order, Integer> {
    List<Order> findByUserUsername(String username);
}
