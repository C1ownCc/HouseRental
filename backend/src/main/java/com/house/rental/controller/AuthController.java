package com.house.rental.controller;

import com.house.rental.entity.User;
import com.house.rental.service.UserService;
import com.house.rental.common.Result;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginForm, HttpSession session) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");
        
        User user = userService.login(username, password);
        if (user != null) {
            // 生成token
            String token = UUID.randomUUID().toString().replace("-", "");
            
            // 将用户信息存储在 session 中
            session.setAttribute("user", user);
            session.setAttribute("token", token);
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userInfo", user);
            return Result.success(data);
        }
        return Result.error("用户名或密码错误");
    }
    
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            return Result.success(registeredUser);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/logout")
    public Result<String> logout(HttpSession session) {
        session.removeAttribute("user");
        session.removeAttribute("token");
        return Result.success("退出登录成功");
    }
    
    @GetMapping("/current-user")
    public Result<User> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return Result.success(user);
        }
        return Result.error("未登录");
    }
} 