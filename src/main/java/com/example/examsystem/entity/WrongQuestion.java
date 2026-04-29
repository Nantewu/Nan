package com.example.examsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("wrong_question")
public class WrongQuestion {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private Long questionId;
    private String questionContent;
    private String userAnswer;
    private String correctAnswer;
    private String knowledgePoint;
    private String module;
    private String subject;
    private String errorReason;
    private LocalDateTime createTime;
    private LocalDateTime lastReviewTime;
    private int reviewCount;
    
    // 新增字段，支持根因溯源
    private String rootCause;
    private String relatedKnowledgePoints;
    private String remediationSuggestion;
    private String semanticAnalysis;
    private double difficulty;
    private int frequency;
}