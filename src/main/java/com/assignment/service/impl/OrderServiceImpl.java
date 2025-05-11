package com.assignment.service.impl;

import com.assignment.dao.OrderDAO;
import com.assignment.entity.*;
import com.assignment.entity.Order;
import com.assignment.service.OrderService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDAO orderDAO;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Order> findAll() {
        return orderDAO.findAll();
    }

    @Override
    public Order findById(Integer id) {
        return orderDAO.findById(id).orElse(null);
    }

    @Override
    public List<Order> findByUsername(String username) {
        return orderDAO.findByUserUsername(username);
    }

    @Override
    public Order save(Order order) {
        return orderDAO.save(order);
    }

    @Override
    public Boolean existsById(Integer id) {
        return orderDAO.existsById(id);
    }

    @Override
    public void deleteById(Integer id) {
        orderDAO.deleteById(id);
    }

    @Override
    public List<Order> searchOrders(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return orderDAO.findAll();
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> root = cq.from(Order.class);
        List<Predicate> predicates = new ArrayList<>();

        // Tìm kiếm theo ID đơn hàng, địa chỉ, trạng thái và tên người dùng
//        predicates.add(cb.like(cb.toString(root.get("id")), "%" + keyword + "%"));
        predicates.add(cb.like(cb.toString(root.get("createDate")), "%" + keyword + "%"));
        predicates.add(cb.like(cb.lower(root.get("status")), "%" + keyword.toLowerCase() + "%"));

        try {
            Integer id = Integer.parseInt(keyword);
            predicates.add(cb.equal(root.get("id"), id));
        } catch (NumberFormatException e) {
            // Bỏ qua nếu keyword không phải số
        }
        // Tìm kiếm theo tên người dùng
        Join<Order, User> userJoin = root.join("user", JoinType.LEFT);
        predicates.add(cb.like(cb.lower(userJoin.get("username")), "%" + keyword.toLowerCase() + "%"));

        // Tính tổng giá trị đơn hàng
        Join<Order, OrderDetail> orderDetailJoin = root.join("orderDetails", JoinType.LEFT);
        Expression<Double> totalPriceExpr = cb.sum(
                cb.prod(orderDetailJoin.get("price"), orderDetailJoin.get("quantity"))
        );

        // Nhóm theo ID của đơn hàng để tính tổng giá trị
        cq.groupBy(root.get("id"));

        // So sánh tổng giá trị với keyword (chuyển keyword thành Double)
        try {
            Double totalPrice = Double.valueOf(keyword);
            double epsilon = 0.01; // khoảng chênh lệch cho phép
            Predicate havingPredicate = cb.between(totalPriceExpr, totalPrice - epsilon, totalPrice + epsilon);
            cq.having(havingPredicate);
            System.out.println(totalPrice + "+" + totalPriceExpr);
        } catch (NumberFormatException e) {
            // Nếu keyword không phải là một số, không cần so sánh với tổng giá trị
        }

        // Cấu hình các điều kiện WHERE và HAVING
        cq.where(cb.or(predicates.toArray(new Predicate[0])));

        // Thực thi truy vấn và trả về kết quả
        return entityManager.createQuery(cq).getResultList();
    }

    private static final Map<Order.OrderStatus, List<Order.OrderStatus>> statusTransitions = new HashMap<>();

    static {
        statusTransitions.put(Order.OrderStatus.PENDING, Arrays.asList(Order.OrderStatus.PROCESSING, Order.OrderStatus.CANCELLED));
        statusTransitions.put(Order.OrderStatus.PROCESSING, Arrays.asList(Order.OrderStatus.SHIPPED, Order.OrderStatus.CANCELLED));
        statusTransitions.put(Order.OrderStatus.SHIPPED, Collections.singletonList(Order.OrderStatus.DELIVERED));
        statusTransitions.put(Order.OrderStatus.DELIVERED, Collections.emptyList());
        statusTransitions.put(Order.OrderStatus.CANCELLED, Collections.emptyList());
    }

    @Override
    public Boolean canUpdateStatus(Order.OrderStatus current, Order.OrderStatus target) {
        return statusTransitions.getOrDefault(current, Collections.emptyList()).contains(target);
    }

}
