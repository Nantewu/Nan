package com.example.examsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.examsystem.entity.User;
import com.example.examsystem.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController 
@RequestMapping("/api/user") 
public class UserController {

    @Autowired
    private UserMapper userMapper;

    /**
     * 登录接口
     * 修改点：返回 "成功|角色|昵称|目标" 格式，便于前端切分跳转
     */
    @GetMapping("/login")
    public String login(String username, String password) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(queryWrapper);
        
        if (user != null && user.getPasswordHash().equals(password)) {
            // 返回标准协议串：成功|角色|昵称|目标
            return "成功|" + user.getRoleId() + "|" + user.getNickname() + "|" + user.getTargetExam();
        } else {
            return "失败：账号或密码错误。";
        }
    }

    /**
     * 注册接口
     */
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());
        
        if (userMapper.selectCount(queryWrapper) > 0) {
            return "注册失败：账号已存在！";
        }

        // 默认初始化设置
        user.setNickname("特工_" + user.getUsername()); 
        user.setTargetExam("未设置");
        user.setRoleId(1L); // 新注册默认为普通用户

        int result = userMapper.insert(user);
        return result > 0 ? "注册成功！" : "注册失败：数据库写入异常。";
    }
    /**
     * 找回密码接口 (通过核对账号和昵称来重置密码)
     */
    @PostMapping("/resetPassword")
    public String resetPassword(String username, String nickname, String newPassword) {
        // 1. 在数据库中同时比对账号和昵称
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username).eq(User::getNickname, nickname);
        User user = userMapper.selectOne(queryWrapper);

        // 2. 如果找到了这个人，就更新密码
        if (user != null) {
            user.setPasswordHash(newPassword);
            int result = userMapper.updateById(user); // 根据 ID 更新该用户的数据
            if (result > 0) {
                return "成功|密码已重置，请返回登录。";
            } else {
                return "失败|数据库更新异常。";
            }
        } else {
            return "失败|账号或特工代号不匹配，无法证明你的身份！";
        }
    }
}