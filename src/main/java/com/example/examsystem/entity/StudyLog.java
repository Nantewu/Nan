package com.example.examsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("study_log")
public class StudyLog {
    @TableId(type = IdType.AUTO, value = "log_id")
    private Long logId;
    
    private Long userId;
    private Long pathId; // 学习路径ID
    private LocalDateTime studyTime;
    private Double masteryScore; // 掌握度
}