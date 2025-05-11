package com.assignment.service;

import com.assignment.entity.Product;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    List<Product> findAll();
    Product findById(Integer id);
    List<Product> findByCategory(Integer categoryId);
    Product save(Product product);
    Boolean existsById(Integer id);
    void deleteById(Integer id);
    List<Product> searchProducts(String keyword);
    List<Product> listTop10Newest();
    List<Product> listMostPurchasedProducts(Pageable pageable);
}
