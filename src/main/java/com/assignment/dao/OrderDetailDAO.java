package com.assignment.dao;

import com.assignment.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface OrderDetailDAO extends JpaRepository<OrderDetail, Integer>{
    @Query("SELECT p.category.name AS category, SUM(od.price * od.quantity) AS totalRevenue " +
            "FROM OrderDetail od " +
            "JOIN od.product p " +
            "WHERE od.order.createDate BETWEEN :startDate AND :endDate " +
            "GROUP BY p.category.name")
    List<Object[]> getRevenueByCategoryAndDateRange(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    @Query("SELECT p.category.name AS category, SUM(od.quantity) AS totalQuantity " +
            "FROM OrderDetail od " +
            "JOIN od.product p " +
            "GROUP BY p.category.name")
    List<Object[]> getQuantityByCategory();
    @Query("SELECT MONTH(o.createDate) AS month, SUM(od.price * od.quantity) AS revenue " +
            "FROM OrderDetail od " +
            "JOIN od.order o " +
            "GROUP BY MONTH(o.createDate) " +
            "ORDER BY MONTH(o.createDate)")
    List<Object[]> getMonthlyRevenue();

}
