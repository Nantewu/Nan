package com.example.examsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("learning_path")
public class LearningPath {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String pathName;
    private String pathType; // 自定义/系统推荐
    private String status; // 进行中/已完成/已暂停
    private LocalDateTime createTime;
    private LocalDateTime lastUpdateTime;
    private int progress; // 完成进度 0-100
    private String targetExam;
    private LocalDateTime targetDate;
    
    // 存储路径详情的JSON字符串
    private String pathDetails;
    
    // 新增字段，支持自适应学习路径
    private String startKpId; // 起始知识点ID
    private String endKpId; // 目标知识点ID
    private int estimatedDuration; // 预计总时长（分钟）
    private double difficultyLevel; // 路径难度等级
    private String optimizationStrategy; // 优化策略
    private String performanceMetrics; // 性能指标
}