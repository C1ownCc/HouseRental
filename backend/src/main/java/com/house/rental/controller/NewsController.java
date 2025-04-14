package com.house.rental.controller;

import com.house.rental.common.response.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/news")
public class NewsController {
    
    @GetMapping("/latest")
    public Result<List<Map<String, Object>>> getLatestNews() {
        List<Map<String, Object>> newsList = new ArrayList<>();
        
        Map<String, Object> news1 = new HashMap<>();
        news1.put("id", 1);
        news1.put("title", "2024年房地产市场展望");
        news1.put("content", "专家预测2024年房地产市场将稳中向好...");
        news1.put("date", "2024-01-01");
        newsList.add(news1);
        
        Map<String, Object> news2 = new HashMap<>();
        news2.put("id", 2);
        news2.put("title", "新房交易政策调整");
        news2.put("content", "多个城市出台新政策支持刚需购房...");
        news2.put("date", "2024-01-02");
        newsList.add(news2);
        
        return Result.success(newsList);
    }
} 