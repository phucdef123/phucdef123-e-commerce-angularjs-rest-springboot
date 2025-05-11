package com.assignment.controller;

import com.assignment.service.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/revenue")
public class RevenueRestController {
    @Autowired
    private RevenueService revenueService;

    @GetMapping("/by-category")
    public List<Object[]> getRevenueByCategoryBetween(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate
    ) {
        return revenueService.getRevenueByCategoryAndDateRange(startDate, endDate);
    }

    @GetMapping("/quantity-by-category")
    public List<Object[]> getQuantityByCategory() {
        return revenueService.getQuantityByCategory();
    }

    @GetMapping("/monthly")
    public List<Object[]> getMonthlyRevenue() {
        return revenueService.getMonthlyRevenue();
    }
}
