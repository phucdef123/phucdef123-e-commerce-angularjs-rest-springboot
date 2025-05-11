package com.assignment.service.impl;

import com.assignment.dao.OrderDAO;
import com.assignment.dao.OrderDetailDAO;
import com.assignment.dao.ProductDAO;
import com.assignment.entity.Order;
import com.assignment.entity.OrderDetail;
import com.assignment.entity.Product;
import com.assignment.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {
    @Autowired
    private OrderDetailDAO orderDetailDAO;
    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private ProductDAO productDAO;

    @Override
    public OrderDetail save(OrderDetail orderDetail) {
        Integer orderId = orderDetail.getOrder().getId();
        Integer productId = orderDetail.getProduct().getId();

        Order order = orderDAO.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        Product product = productDAO.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        // Gán lại vào OrderDetail
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        return orderDetailDAO.save(orderDetail);
    }

    @Override
    public Boolean existsById(Integer id) {
        return orderDetailDAO.existsById(id);
    }
}
