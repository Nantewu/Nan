package com.example.examsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("answer_record")
public class AnswerRecord {
    @TableId(type = IdType.AUTO, value = "record_id")
    private Long recordId;
    
    private Long userId;
    private Long kpId; // 知识点ID
    private Long questionId;
    private Boolean isCorrect;
    private LocalDateTime answerTime;
    private Integer useTime; // 答题用时（秒）
}