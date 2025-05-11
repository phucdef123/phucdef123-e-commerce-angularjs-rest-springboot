package com.assignment.dao;

import com.assignment.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryDAO extends JpaRepository<Category, Integer>{
    Boolean existsByName(String name);

//    List<Category> find;
}
