package com.house.rental.controller;

import com.house.rental.common.Result;
import com.house.rental.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = dashboardService.getStats();
        return Result.success(stats);
    }
    
    @GetMapping("/trend")
    public Result<Map<String, Object>> getTrend() {
        Map<String, Object> trend = dashboardService.getTrend();
        return Result.success(trend);
    }
    
    @GetMapping("/distribution")
    public Result<Map<String, Object>> getDistribution() {
        Map<String, Object> distribution = dashboardService.getDistribution();
        return Result.success(distribution);
    }
} 