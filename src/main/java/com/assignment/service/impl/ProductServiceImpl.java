package com.assignment.service.impl;

import com.assignment.dao.ProductDAO;
import com.assignment.entity.Category;
import com.assignment.entity.Product;
import com.assignment.service.ProductService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDAO productDAO;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Product> findAll() {
        return productDAO.findAll();
    }

    @Override
    public Product findById(Integer id) {
        return productDAO.findById(id).orElse(null);
    }

    @Override
    public List<Product> findByCategory(Integer categoryId) {
        return productDAO.findByCategoryId(categoryId);
    }

    @Override
    public Product save(Product product) {
        return productDAO.save(product);
    }

    @Override
    public Boolean existsById(Integer id) {
        return productDAO.existsById(id);
    }

    @Override
    public void deleteById(Integer id) {
        productDAO.deleteById(id);
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productDAO.findAll();
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        List<Predicate> predicates = new ArrayList<>();

        // Tìm kiếm trên các cột kiểu String
        predicates.add(cb.like(cb.toString(root.get("id")), "%" + keyword + "%"));
        predicates.add(cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
        //predicates.add(cb.like(cb.lower(root.get("image")), "%" + keyword.toLowerCase() + "%"));
        //predicates.add(cb.like(cb.lower(root.get("description")), "%" + keyword.toLowerCase() + "%"));
        predicates.add(cb.like(cb.toString(root.get("price")), "%" + keyword + "%"));
        predicates.add(cb.like(cb.toString(root.get("createDate")), "%" + keyword + "%"));

        // Tìm kiếm trên cột Integer (id)
//        try {
//            Integer id = Integer.parseInt(keyword);
//            predicates.add(cb.equal(root.get("id"), id));
//        } catch (NumberFormatException e) {
//            // Bỏ qua nếu keyword không phải số
//        }

        // Tìm kiếm trên cột Date (createDate)
//        try {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            Date date = dateFormat.parse(keyword);
//
//            // Tạo khoảng từ 00:00:00 đến 23:59:59 của ngày đó
//            Date startOfDay = date;
//            Date endOfDay = new Date(date.getTime() + 24 * 60 * 60 * 1000); // Cộng thêm 1 ngày
//
//            predicates.add(cb.greaterThanOrEqualTo(root.get("createDate"), startOfDay));
//            predicates.add(cb.lessThan(root.get("createDate"), endOfDay));
//        } catch (ParseException e) {
//            // Bỏ qua nếu keyword không phải định dạng ngày
//        }


        // Tìm kiếm trên cột Boolean (available)
        if ("true".equalsIgnoreCase(keyword) || "false".equalsIgnoreCase(keyword)) {
            Boolean available = Boolean.parseBoolean(keyword);
            predicates.add(cb.equal(root.get("available"), available));
        }

        // Tìm kiếm trên cột quan hệ ManyToOne (category.name)
        Join<Product, Category> categoryJoin = root.join("category", JoinType.LEFT);
        predicates.add(cb.like(cb.lower(categoryJoin.get("name")), "%" + keyword.toLowerCase() + "%"));

        // Kết hợp tất cả điều kiện bằng OR
        cq.where(cb.or(predicates.toArray(new Predicate[0])));

        // Thực thi truy vấn
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<Product> listTop10Newest() {
        return productDAO.findTop10ByOrderByCreateDateDesc();
    }

    @Override
    public List<Product> listMostPurchasedProducts(Pageable pageable) {
        return productDAO.findMostPurchasedProducts(pageable);
    }
}
