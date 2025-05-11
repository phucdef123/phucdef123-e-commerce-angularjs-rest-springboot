package com.assignment.service;

import com.assignment.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> findAll();
    Category findById(Integer id);
    Category save(Category category);
    Boolean existsById(Integer id);
    Boolean existsByName(String name);
    void deleteById(Integer id);
    List<Category> searchCategories(String keyword);
}
