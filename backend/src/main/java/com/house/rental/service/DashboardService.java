package com.house.rental.service;

import java.util.Map;

public interface DashboardService {
    /**
     * 获取统计数据
     */
    Map<String, Object> getStats();
    
    /**
     * 获取趋势数据
     */
    Map<String, Object> getTrend();
    
    /**
     * 获取分布数据
     */
    Map<String, Object> getDistribution();
} 