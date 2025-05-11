package com.assignment.service.impl;

import com.assignment.dao.CategoryDAO;
import com.assignment.entity.Category;
import com.assignment.entity.Product;
import com.assignment.service.CategoryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryDAO categoryDAO;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Category> findAll() {
        return categoryDAO.findAll();
    }

    @Override
    public Category findById(Integer id) {
        return categoryDAO.findById(id).orElse(null);
    }

    @Override
    public Category save(Category category) {
        return categoryDAO.save(category);
    }

    @Override
    public Boolean existsById(Integer id) {
        return categoryDAO.existsById(id);
    }

    @Override
    public Boolean existsByName(String name) {
        return categoryDAO.existsByName(name);
    }

    @Override
    public void deleteById(Integer id) {
        categoryDAO.deleteById(id);
    }

    @Override
    public List<Category> searchCategories(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return categoryDAO.findAll();
        }
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Category> cq = cb.createQuery(Category.class);
        Root<Category> root = cq.from(Category.class);
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.like(cb.toString(root.get("id")), "%" + keyword + "%"));
        predicates.add(cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));

        cq.where(cb.or(predicates.toArray(new Predicate[0])));

        // Thực thi truy vấn
        return entityManager.createQuery(cq).getResultList();
    }
}
