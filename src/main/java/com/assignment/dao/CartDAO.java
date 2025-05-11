package com.assignment.dao;

import com.assignment.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartDAO extends JpaRepository<Cart, Integer> {
    List<Cart> findByUserUsername(String username);
}
