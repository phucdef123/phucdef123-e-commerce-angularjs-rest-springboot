package com.assignment.service.impl;

import com.assignment.dao.CartDAO;
import com.assignment.entity.Cart;
import com.assignment.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartDAO cartDAO;

    @Override
    public List<Cart> findAll() {
        return cartDAO.findAll();
    }

    @Override
    public Cart findById(Integer id) {
        return cartDAO.findById(id).orElse(null);
    }

    @Override
    public Cart save(Cart cart) {
        return cartDAO.save(cart);
    }

    @Override
    public Boolean existsById(Integer id) {
        return cartDAO.existsById(id);
    }

    @Override
    public void deleteById(Integer id) {
        cartDAO.deleteById(id);
    }

    @Override
    public List<Cart> findByUsername(String username) {
        return cartDAO.findByUserUsername(username);
    }
}
