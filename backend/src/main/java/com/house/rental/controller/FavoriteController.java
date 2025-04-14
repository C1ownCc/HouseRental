package com.house.rental.controller;

import com.house.rental.common.Result;
import com.house.rental.entity.Favorite;
import com.house.rental.entity.User;
import com.house.rental.service.FavoriteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    @PostMapping("/{houseId}")
    public Result<String> addFavorite(@PathVariable Long houseId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return Result.error("未登录");
            }
            
            favoriteService.addFavorite(user.getId(), houseId);
            return Result.success("收藏成功");
        } catch (Exception e) {
            return Result.error("收藏失败：" + e.getMessage());
        }
    }
    
    @DeleteMapping("/{houseId}")
    public Result<String> removeFavorite(@PathVariable Long houseId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return Result.error("未登录");
            }
            
            favoriteService.removeFavorite(user.getId(), houseId);
            return Result.success("取消收藏成功");
        } catch (Exception e) {
            return Result.error("取消收藏失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/list")
    public Result<Map<String, Object>> getFavoriteList(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return Result.error("未登录");
            }
            
            List<Favorite> favorites = favoriteService.getUserFavorites(user.getId());
            return Result.success(Map.of(
                    "list", favorites,
                    "total", favorites.size()
            ));
        } catch (Exception e) {
            return Result.error("获取收藏列表失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/check/{houseId}")
    public Result<Boolean> isFavorite(@PathVariable Long houseId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return Result.error("未登录");
            }
            
            boolean isFavorite = favoriteService.isFavorite(user.getId(), houseId);
            return Result.success(isFavorite);
        } catch (Exception e) {
            return Result.error("检查收藏状态失败：" + e.getMessage());
        }
    }
} 