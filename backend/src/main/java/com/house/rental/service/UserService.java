package com.house.rental.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.house.rental.entity.User;

import java.util.List;

public interface UserService extends IService<User> {
    /**
     * 用户登录
     */
    User login(String username, String password);
    
    /**
     * 用户注册
     */
    User register(User user);
    
    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);
    
    /**
     * 更新用户信息
     */
    void updateUser(User user);
    
    User getById(Long id);
    
    /**
     * 获取用户列表
     */
    Page<User> getUserList(Integer page, Integer size, String keyword, String role, Integer status);
    
    /**
     * 更新用户信息（管理员）
     */
    void updateUserByAdmin(User user);
    
    /**
     * 更新用户状态
     */
    void updateUserStatus(Long id, Integer status);
    
    /**
     * 更新用户角色
     */
    void updateUserRole(Long id, String role);
    
    /**
     * 删除用户
     */
    void deleteUser(Long id);
    
    /**
     * 批量删除用户
     */
    void batchDeleteUsers(List<Long> ids);
    
    /**
     * 批量更新用户状态
     */
    void batchUpdateStatus(List<Long> ids, Integer status);
    
    /**
     * 获取热门房东列表
     */
    List<User> getPopularLandlords();
} 