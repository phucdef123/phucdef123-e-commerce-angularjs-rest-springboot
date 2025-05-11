package com.assignment.service;

import com.assignment.entity.Cart;

import java.util.List;

public interface CartService {
    List<Cart> findAll();
    Cart findById(Integer id);
    Cart save(Cart cart);
    Boolean existsById(Integer id);
    void deleteById(Integer id);
    List<Cart> findByUsername(String username);
}
