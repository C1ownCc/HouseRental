package com.house.rental.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.house.rental.common.response.Result;
import com.house.rental.entity.User;
import com.house.rental.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取用户个人信息
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getById(id);
            if (user != null) {
                user.setPassword(null); // 清除敏感信息
                return Result.success(user);
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping
    public Result<Void> updateUser(@RequestBody User user) {
        try {
            userService.updateUser(user);
            return Result.success();
        } catch (Exception e) {
            return Result.error("更新用户信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 分页查询用户列表
     * @param page 当前页
     * @param size 每页大小
     * @param keyword 关键词
     * @param role 角色
     * @param status 状态
     * @return 用户列表
     */
    @GetMapping("/list")
    public Result<Page<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status) {
        try {
            Page<User> userPage = userService.getUserList(page, size, keyword, role, status);
            return Result.success(userPage);
        } catch (Exception e) {
            return Result.error("获取用户列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新用户信息（管理员）
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping("/admin")
    public Result<Void> updateUserByAdmin(@RequestBody User user) {
        try {
            userService.updateUserByAdmin(user);
            return Result.success();
        } catch (Exception e) {
            return Result.error("更新用户信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新用户状态
     * @param id 用户ID
     * @param status 状态
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        try {
            userService.updateUserStatus(id, status);
            return Result.success();
        } catch (Exception e) {
            return Result.error("更新用户状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新用户角色
     * @param id 用户ID
     * @param role 角色
     * @return 更新结果
     */
    @PutMapping("/{id}/role")
    public Result<Void> updateUserRole(
            @PathVariable Long id,
            @RequestParam String role) {
        try {
            userService.updateUserRole(id, role);
            return Result.success();
        } catch (Exception e) {
            return Result.error("更新用户角色失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error("删除用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量删除用户
     * @param ids 用户ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteUsers(@RequestBody List<Long> ids) {
        try {
            userService.batchDeleteUsers(ids);
            return Result.success();
        } catch (Exception e) {
            return Result.error("批量删除用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量更新用户状态
     * @param ids 用户ID列表
     * @param status 状态
     * @return 更新结果
     */
    @PutMapping("/batch/status")
    public Result<Void> batchUpdateStatus(
            @RequestBody List<Long> ids,
            @RequestParam Integer status) {
        try {
            userService.batchUpdateStatus(ids, status);
            return Result.success();
        } catch (Exception e) {
            return Result.error("批量更新用户状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取热门房东列表
     * @return 热门房东列表
     */
    @GetMapping("/popular-landlords")
    public Result<List<User>> getPopularLandlords() {
        try {
            List<User> landlords = userService.getPopularLandlords();
            return Result.success(landlords);
        } catch (Exception e) {
            return Result.error("获取热门房东列表失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestBody User user, HttpSession session) {
        try {
            log.info("开始更新用户信息 ====================");
            log.info("请求体: {}", user);
            
            // 从session获取当前用户
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                log.warn("未登录");
                return Result.error("未登录");
            }
            
            // 设置ID，确保只能修改自己的信息
            user.setId(currentUser.getId());
            user.setUsername(currentUser.getUsername()); // 用户名不允许修改
            user.setRole(currentUser.getRole()); // 角色不允许修改
            user.setStatus(currentUser.getStatus()); // 状态不允许修改
            user.setDeleted(currentUser.getDeleted()); // 删除标记不允许修改
            
            log.info("更新前的用户信息: {}", user);
            
            // 更新用户信息
            userService.updateUser(user);
            
            log.info("更新后的用户信息: {}", user);
            
            // 更新session中的用户信息
            user.setPassword(null); // 清除密码
            session.setAttribute("user", user);
            
            log.info("用户信息更新完成 ====================");
            
            return Result.success(user);
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return Result.error("更新失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/password")
    public Result<String> updatePassword(@RequestBody Map<String, String> passwordForm, HttpSession session) {
        try {
            // 从session获取当前用户
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                return Result.error("未登录");
            }
            
            String oldPassword = passwordForm.get("oldPassword");
            String newPassword = passwordForm.get("newPassword");
            
            // 从数据库重新获取用户信息，确保密码信息是最新的
            User dbUser = userService.getById(currentUser.getId());
            if (dbUser == null) {
                return Result.error("用户不存在");
            }
            
            // 验证旧密码
            String oldPasswordMd5 = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
            if (!oldPasswordMd5.equals(dbUser.getPassword())) {
                return Result.error("原密码错误");
            }
            
            // 更新密码
            dbUser.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
            userService.updateUser(dbUser);
            
            return Result.success("密码修改成功");
        } catch (Exception e) {
            return Result.error("修改失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error("未登录");
        }
        return Result.success(user);
    }
} 