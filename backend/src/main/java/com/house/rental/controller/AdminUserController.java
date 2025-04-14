package com.house.rental.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.house.rental.common.Result;
import com.house.rental.entity.User;
import com.house.rental.service.UserService;
import com.house.rental.utils.UserContext;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final UserService userService;

    @PostMapping("/add")
    public Result<String> createUser(@RequestBody User user) {
        try {
            userService.register(user);
            return Result.success("添加成功");
        } catch (Exception e) {
            return Result.error("添加失败：" + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status
    ) {
        try {
            Page<User> userPage = userService.getUserList(page, size, keyword, role, status);
            return Result.success(Map.of(
                    "list", userPage.getRecords(),
                    "total", userPage.getTotal()
            ));
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return Result.error("获取用户列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }
            user.setPassword(null); // 清除密码
            return Result.success(user);
        } catch (Exception e) {
            return Result.error("获取用户信息失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<String> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            user.setId(id);
            userService.updateUserByAdmin(user);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public Result<String> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status
    ) {
        try {
            userService.updateUserStatus(id, status);
            return Result.success("状态更新成功");
        } catch (Exception e) {
            return Result.error("状态更新失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}/role")
    public Result<String> updateUserRole(
            @PathVariable Long id,
            @RequestParam String role
    ) {
        try {
            userService.updateUserRole(id, role);
            return Result.success("角色更新成功");
        } catch (Exception e) {
            return Result.error("角色更新失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    @PostMapping("/batch-delete")
    public Result<String> batchDeleteUsers(@RequestBody List<Long> ids) {
        try {
            userService.batchDeleteUsers(ids);
            return Result.success("批量删除成功");
        } catch (Exception e) {
            return Result.error("批量删除失败：" + e.getMessage());
        }
    }

    @PutMapping("/batch-status")
    public Result<String> batchUpdateStatus(
            @RequestBody List<Long> ids,
            @RequestParam Integer status
    ) {
        try {
            userService.batchUpdateStatus(ids, status);
            return Result.success("批量更新状态成功");
        } catch (Exception e) {
            return Result.error("批量更新状态失败：" + e.getMessage());
        }
    }
} 