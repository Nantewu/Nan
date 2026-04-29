package com.example.examsystem.controller;

import com.example.examsystem.entity.WrongQuestion;
import com.example.examsystem.service.WrongQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wrong-question")
public class WrongQuestionController {
    
    @Autowired
    private WrongQuestionService wrongQuestionService;
    
    /**
     * 添加入错题本
     */
    @PostMapping("/add")
    public boolean addWrongQuestion(@RequestBody WrongQuestion wrongQuestion) {
        return wrongQuestionService.addWrongQuestion(wrongQuestion);
    }
    
    /**
     * 获取用户的错题列表
     */
    @GetMapping("/user/{userId}")
    public List<WrongQuestion> getUserWrongQuestions(@PathVariable Long userId) {
        return wrongQuestionService.getUserWrongQuestions(userId);
    }
    
    /**
     * 深度错题分析
     */
    @GetMapping("/analyze/{userId}")
    public Map<String, Object> analyzeWrongQuestions(@PathVariable Long userId) {
        return wrongQuestionService.analyzeWrongQuestions(userId);
    }
    
    /**
     * 复习错题
     */
    @PutMapping("/review/{id}")
    public boolean reviewWrongQuestion(@PathVariable Long id) {
        return wrongQuestionService.reviewWrongQuestion(id);
    }
    
    /**
     * 错题根因分析
     */
    @GetMapping("/analyze")
    public Map<String, Object> analyzeWrongQuestion(@RequestParam Long wrongQuestionId) {
        return wrongQuestionService.getRootCauseAnalysis(wrongQuestionId);
    }
}