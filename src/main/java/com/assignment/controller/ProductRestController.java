package com.assignment.controller;

import com.assignment.entity.Product;
import com.assignment.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/products")
public class ProductRestController {
    @Autowired
    ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<Product> get(@PathVariable("id") Integer id) {
        if (id == null || id <= 0 ) {
            return ResponseEntity.badRequest().build();
        }
        if (!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productService.findById(id));
    }
    @GetMapping
    public ResponseEntity<List<Product>> searchProducts(@RequestParam(required = false) String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }
    @PostMapping
    public ResponseEntity<Product> post(@RequestBody Product product) {
        if (product.getCategory() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (product.getId() != null && productService.existsById(product.getId())) {
            return ResponseEntity.notFound().build();
        }
        product.setId(null);
        return ResponseEntity.ok(productService.save(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> put(@RequestBody Product product, @PathVariable("id") Integer id) {
        if (product.getId() == null || product.getCategory() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        product.setId(id);
        return ResponseEntity.ok(productService.save(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        if (id == null || id <= 0 ) {
            return ResponseEntity.badRequest().build();
        }
        if (!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
