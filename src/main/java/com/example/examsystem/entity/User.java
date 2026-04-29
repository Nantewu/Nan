package com.example.examsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO, value = "user_id")
    private Long userId;
    
    private String username;
    private String passwordHash;
    private LocalDateTime registerTime;
    private Boolean isActive;
    private Long roleId;
    
    // 扩展字段
    private String nickname;
    private String targetExam;
}