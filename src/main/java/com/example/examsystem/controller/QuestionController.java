package com.example.examsystem.controller;

import com.example.examsystem.entity.AnswerRecord;
import com.example.examsystem.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/question")
public class QuestionController {
    
    @Autowired
    private QuestionService questionService;
    
    /**
     * 提交答案
     */
    @PostMapping("/submit")
    public boolean submitAnswer(@RequestBody AnswerRecord answerRecord) {
        return questionService.submitAnswer(answerRecord);
    }
    
    /**
     * 获取用户答题记录
     */
    @GetMapping("/records/{userId}")
    public List<AnswerRecord> getUserAnswerRecords(@PathVariable Long userId) {
        return questionService.getUserAnswerRecords(userId);
    }
    
    /**
     * 个性化题组推荐
     */
    @GetMapping("/recommend")
    public List<Map<String, Object>> recommendQuestions(@RequestParam Long userId, @RequestParam int limit) {
        return questionService.recommendQuestions(userId, limit);
    }
    
    /**
     * 基于知识点推荐题目
     */
    @GetMapping("/recommend/kp")
    public List<Map<String, Object>> recommendByKnowledgePoint(@RequestParam String kpId, @RequestParam int limit) {
        return questionService.recommendByKnowledgePoint(kpId, limit);
    }
    
    /**
     * 基于模块推荐题目
     */
    @GetMapping("/recommend/module")
    public List<Map<String, Object>> recommendByModule(@RequestParam String module, @RequestParam int limit) {
        return questionService.recommendByModule(module, limit);
    }
}