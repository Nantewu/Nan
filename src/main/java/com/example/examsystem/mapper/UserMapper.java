package com.example.examsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.examsystem.entity.User;
import com.example.examsystem.entity.Role;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 根据用户名查询用户
    User selectByUsername(String username);
    
    // 根据用户ID查询角色信息
    Role selectRoleByUserId(Long userId);
}