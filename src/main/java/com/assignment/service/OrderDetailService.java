package com.assignment.service;

import com.assignment.entity.OrderDetail;

public interface OrderDetailService {
    OrderDetail save(OrderDetail orderDetail);
    Boolean existsById(Integer id);
}
