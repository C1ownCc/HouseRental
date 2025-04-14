package com.house.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.house.rental.common.exception.BusinessException;
import com.house.rental.entity.Message;
import com.house.rental.entity.User;
import com.house.rental.mapper.UserMapper;
import com.house.rental.service.MessageService;
import com.house.rental.service.UserService;
import com.house.rental.common.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private MessageService messageService;
    
    @Override
    public User login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        
        String inputPasswordMd5 = DigestUtils.md5DigestAsHex(password.getBytes());
        log.info("登录验证 - 用户名: {}, 输入密码MD5: {}, 数据库密码: {}", username, inputPasswordMd5, user != null ? user.getPassword() : "null");
        
        if (user != null && user.getPassword().equals(inputPasswordMd5)) {
            // 检查用户状态
            if (user.getStatus() == 0) {
                throw new ServiceException("账号已被禁用，请联系管理员");
            }
            user.setPassword(null); // 清空密码
            return user;
        }
        return null;
    }
    
    @Override
    @Transactional
    public User register(User user) {
        log.info("开始注册用户: {}", user.getUsername());
        
        // 检查用户名是否已存在
        if (getByUsername(user.getUsername()) != null) {
            log.warn("用户名已存在: {}", user.getUsername());
            throw new ServiceException("用户名已存在");
        }
        
        // 使用MD5加密密码
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        
        // 设置默认值
        user.setStatus(1);  // 默认启用状态
        user.setDeleted(0); // 未删除
        
        // 设置默认角色
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("user");
        }
        
        try {
            userMapper.insert(user);
            log.info("用户注册成功: {}", user.getUsername());
            
            // 发送欢迎消息
            Message welcomeMessage = new Message();
            welcomeMessage.setUserId(user.getId());
            welcomeMessage.setType(1);  // 系统消息
            welcomeMessage.setTitle("欢迎加入");
            welcomeMessage.setContent("欢迎使用房屋租赁平台！");
            messageService.createMessage(welcomeMessage);
            
            user.setPassword(null); // 清空密码
            return user;
        } catch (org.springframework.dao.DuplicateKeyException e) {
            log.error("用户注册失败: {}", user.getUsername(), e);
            throw new ServiceException("用户名已存在");
        } catch (Exception e) {
            log.error("用户注册失败: {}", user.getUsername(), e);
            throw new ServiceException("注册失败，请稍后重试");
        }
    }
    
    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }
    
    @Override
    @Transactional
    public void updateUser(User user) {
    
        
        // 获取原用户信息
        User dbUser = userMapper.selectById(user.getId());
        if (dbUser == null) {
            log.error("用户不存在 - ID: {}", user.getId());
            throw new RuntimeException("用户不存在");
        }
        
        // 保留不允许修改的字段
        user.setUsername(dbUser.getUsername());
        user.setRole(dbUser.getRole());
        user.setStatus(dbUser.getStatus());
        user.setDeleted(dbUser.getDeleted());
        user.setCreatedTime(dbUser.getCreatedTime());
        
        // 设置更新时间为当前时间
        user.setUpdatedTime(LocalDateTime.now());
        
        // 如果没有设置新密码，保留原密码
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(dbUser.getPassword());
        }
        
        // 处理头像路径
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            // 如果头像路径以 /images/ 开头，则添加 /uploads 前缀
            if (user.getAvatar().startsWith("/images/")) {
                user.setAvatar("/uploads" + user.getAvatar());
            }
        }
        
        // 更新用户信息
        int result = userMapper.updateById(user);
        log.info("更新结果: {}", result);
        log.info("更新后的头像路径: {}", user.getAvatar());
        log.info("用户信息更新完成 ====================");

        // 发送个人信息更新通知
        Message updateMessage = new Message();
        updateMessage.setUserId(user.getId());
        updateMessage.setType(1);  // 系统消息
        updateMessage.setTitle("个人信息更新通知");
        
        StringBuilder content = new StringBuilder("您的个人信息已更新：");
        if (!dbUser.getNickname().equals(user.getNickname())) {
            content.append("昵称已更新；");
        }
        if (!dbUser.getEmail().equals(user.getEmail())) {
            content.append("邮箱已更新；");
        }
        if (!dbUser.getPhone().equals(user.getPhone())) {
            content.append("手机号已更新；");
        }
        if ((dbUser.getAvatar() == null && user.getAvatar() != null) || 
            (dbUser.getAvatar() != null && !dbUser.getAvatar().equals(user.getAvatar()))) {
            content.append("头像已更新；");
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty() && 
            !user.getPassword().equals(dbUser.getPassword())) {
            content.append("密码已更新；");
        }
        
        updateMessage.setContent(content.toString());
        messageService.createMessage(updateMessage);
    }
    
    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public Page<User> getUserList(Integer page, Integer size, String keyword, String role, Integer status) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        // 添加搜索条件
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or()
                    .like(User::getNickname, keyword)
                    .or()
                    .like(User::getPhone, keyword)
                    .or()
                    .like(User::getEmail, keyword));
        }
        
        // 角色筛选
        if (StringUtils.hasText(role)) {
            wrapper.eq(User::getRole, role);
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        
        // 只查询未删除的用户
        wrapper.eq(User::getDeleted, 0);
        
        // 按创建时间倒序排序
        wrapper.orderByDesc(User::getCreatedTime);
        
        Page<User> userPage = userMapper.selectPage(pageParam, wrapper);
        
        // 清除密码
        userPage.getRecords().forEach(user -> user.setPassword(null));
        
        return userPage;
    }

    @Override
    @Transactional
    public void updateUserByAdmin(User user) {
        // 获取原用户信息
        User dbUser = userMapper.selectById(user.getId());
        if (dbUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 保留不允许修改的字段
        user.setUsername(dbUser.getUsername());  // 用户名不允许修改
        user.setPassword(dbUser.getPassword());  // 密码不在这里修改
        user.setDeleted(dbUser.getDeleted());   // 删除标记不允许修改
        user.setCreatedTime(dbUser.getCreatedTime());
        user.setUpdatedTime(LocalDateTime.now());
        
        // 处理头像路径
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            // 如果头像路径以 /images/ 开头，则添加 /uploads 前缀
            if (user.getAvatar().startsWith("/images/")) {
                user.setAvatar("/uploads" + user.getAvatar());
            }
        }
        
        userMapper.updateById(user);
        
        // 发送用户信息更新通知
        Message updateMessage = new Message();
        updateMessage.setUserId(user.getId());
        updateMessage.setType(1);  // 系统消息
        updateMessage.setTitle("账号信息变更通知");
        updateMessage.setContent("您的账号信息已被管理员更新，请注意查看。");
        messageService.createMessage(updateMessage);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long id, Integer status) {
        // 验证状态值
        if (status != 0 && status != 1) {
            throw new RuntimeException("无效的状态值，只能是 0(禁用) 或 1(启用)");
        }

        User user = new User();
        user.setId(id);
        user.setStatus(status);
        user.setUpdatedTime(LocalDateTime.now());
        
        userMapper.updateById(user);
        
        // 发送状态变更通知
        Message statusMessage = new Message();
        statusMessage.setUserId(id);
        statusMessage.setType(1);  // 系统消息
        statusMessage.setTitle("账号状态变更通知");
        statusMessage.setContent("您的账号已" + (status == 1 ? "启用" : "禁用"));
        messageService.createMessage(statusMessage);
    }

    @Override
    @Transactional
    public void updateUserRole(Long id, String role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        user.setUpdatedTime(LocalDateTime.now());
        
        userMapper.updateById(user);
        
        // 发送角色变更通知
        Message roleMessage = new Message();
        roleMessage.setUserId(id);
        roleMessage.setType(1);  // 系统消息
        roleMessage.setTitle("账号角色变更通知");
        roleMessage.setContent("您的账号角色已变更为：" + role);
        messageService.createMessage(roleMessage);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        // 获取原用户信息，确保用户存在
        User dbUser = userMapper.selectById(id);
        if (dbUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 直接删除用户
        int result = userMapper.deleteById(id);
        if (result != 1) {
            throw new RuntimeException("删除用户失败");
        }
        
        // 发送账号删除通知
        Message deleteMessage = new Message();
        deleteMessage.setUserId(id);
        deleteMessage.setType(1);  // 系统消息
        deleteMessage.setTitle("账号删除通知");
        deleteMessage.setContent("您的账号已被管理员删除。");
        messageService.createMessage(deleteMessage);
    }

    @Override
    @Transactional
    public void batchDeleteUsers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        
        for (Long id : ids) {
            try {
                deleteUser(id);
            } catch (Exception e) {
                log.error("批量删除用户失败，ID: " + id, e);
                // 继续处理其他用户
            }
        }
    }

    @Override
    @Transactional
    public void batchUpdateStatus(List<Long> ids, Integer status) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要操作的用户");
        }
        if (status == null) {
            throw new BusinessException("请选择要更新的状态");
        }
        
        List<User> users = userMapper.selectBatchIds(ids);
        for (User user : users) {
            user.setStatus(status);
            userMapper.updateById(user);
        }
    }

    @Override
    public List<User> getPopularLandlords() {
        // 创建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(User::getRole, "landlord", "admin")  // 查询房东和管理员
                .eq(User::getStatus, 1)  // 只查询正常状态的用户
                .eq(User::getDeleted, 0)  // 只查询未删除的用户
                .orderByDesc(User::getCreatedTime)  // 按创建时间倒序
                .last("LIMIT 4");  // 限制返回4条记录
        
        // 查询并返回结果
        return userMapper.selectList(wrapper);
    }
} 