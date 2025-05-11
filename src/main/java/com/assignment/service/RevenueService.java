package com.assignment.service;

import com.assignment.dao.OrderDetailDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RevenueService {
    @Autowired
    private OrderDetailDAO orderDetailDAO;

    public List<Object[]> getRevenueByCategoryAndDateRange(Date startDate, Date endDate) {
        return orderDetailDAO.getRevenueByCategoryAndDateRange(startDate, endDate);
    }
    public List<Object[]> getQuantityByCategory() {
        return orderDetailDAO.getQuantityByCategory();
    }
    public List<Object[]> getMonthlyRevenue() {
        return orderDetailDAO.getMonthlyRevenue();
    }
}
