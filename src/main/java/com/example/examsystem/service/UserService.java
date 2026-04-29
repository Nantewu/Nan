package com.example.examsystem.service;

import com.example.examsystem.entity.User;
import com.example.examsystem.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 根据用户名查找用户
     */
    public User findByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
    
    /**
     * 根据用户ID查找用户
     */
    public User findById(Long id) {
        return userMapper.selectById(id);
    }
    
    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        return userMapper.selectList(null);
    }
    
    /**
     * 注册用户
     */
    public boolean register(User user) {
        // 检查用户名是否已存在
        if (findByUsername(user.getUsername()) != null) {
            return false;
        }
        // 密码加密（实际项目中应该使用加密算法）
        user.setPasswordHash(user.getPasswordHash());
        // 设置注册时间
        user.setRegisterTime(java.time.LocalDateTime.now());
        // 设置默认状态为激活
        user.setIsActive(true);
        // 设置默认角色为学生
        user.setRoleId(1L);
        return userMapper.insert(user) > 0;
    }
    
    /**
     * 添加用户（管理员功能）
     */
    public boolean addUser(User user) {
        // 检查用户名是否已存在
        if (findByUsername(user.getUsername()) != null) {
            return false;
        }
        // 密码加密（实际项目中应该使用加密算法）
        user.setPasswordHash(user.getPasswordHash());
        // 设置注册时间
        user.setRegisterTime(java.time.LocalDateTime.now());
        return userMapper.insert(user) > 0;
    }
    
    /**
     * 更新用户信息
     */
    public boolean updateUser(User user) {
        return userMapper.updateById(user) > 0;
    }
    
    /**
     * 删除用户
     */
    public boolean deleteUser(Long userId) {
        return userMapper.deleteById(userId) > 0;
    }
    
    /**
     * 登录验证
     */
    public User login(String username, String password) {
        User user = findByUsername(username);
        if (user != null && user.getPasswordHash().equals(password)) {
            return user;
        }
        return null;
    }
}