package com.assignment.dao;

import com.assignment.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductDAO extends JpaRepository<Product, Integer>{
    List<Product> findByCategoryId(Integer categoryId);
    List<Product> findTop10ByOrderByCreateDateDesc();
    @Query("SELECT p FROM Product p JOIN OrderDetail od ON p.id = od.product.id " +
            "GROUP BY p ORDER BY SUM(od.quantity) DESC")
    List<Product> findMostPurchasedProducts(Pageable pageable);

}
