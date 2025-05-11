package com.assignment.controller;

import com.assignment.entity.Category;
import com.assignment.entity.Product;
import com.assignment.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/admin/categories")
public class CategoryRestController {
    @Autowired
    CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> searchCategories(@RequestParam(required = false) String keyword) {
        List<Category> categories = categoryService.searchCategories(keyword);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable("id") Integer id) {
        if (id == null || id <= 0 ) {
            return ResponseEntity.badRequest().build();
        }
        if (!categoryService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Category> post(@RequestBody Category category) {
        if (category == null || category.getName() == null || category.getName().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (categoryService.existsByName(category.getName())) {
            return ResponseEntity.badRequest().build();
        }
        category.setId(null);
        return ResponseEntity.ok(categoryService.save(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> put(@RequestBody Category category, @PathVariable("id") Integer id) {
        if (category == null || category.getName() == null || category.getName().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (!categoryService.existsById(id) || categoryService.existsByName(category.getName())) {
            return ResponseEntity.notFound().build();
        }

        category.setId(id);
        return ResponseEntity.ok(categoryService.save(category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        if (id == null || id <= 0 ) {
            return ResponseEntity.badRequest().build();
        }
        if (!categoryService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
