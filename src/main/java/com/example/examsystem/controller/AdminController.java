package com.example.examsystem.controller;

import com.example.examsystem.entity.User;
import com.example.examsystem.entity.Role;
import com.example.examsystem.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private KnowledgeGraphService knowledgeGraphService;
    
    @Autowired
    private QuestionService questionService;
    
    @Autowired
    private LearningPathService learningPathService;
    
    @Autowired
    private WrongQuestionService wrongQuestionService;
    
    /**
     * 用户管理
     */
    // 获取所有用户
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    
    // 添加用户
    @PostMapping("/users")
    public boolean addUser(@RequestBody User user) {
        return userService.addUser(user);
    }
    
    // 更新用户
    @PutMapping("/users/{userId}")
    public boolean updateUser(@PathVariable Long userId, @RequestBody User user) {
        user.setUserId(userId);
        return userService.updateUser(user);
    }
    
    // 删除用户
    @DeleteMapping("/users/{userId}")
    public boolean deleteUser(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }
    
    /**
     * 知识图谱管理
     */
    // 获取所有知识点
    @GetMapping("/knowledge-points")
    public List<com.example.examsystem.KnowledgePoint> getAllKnowledgePoints() {
        return knowledgeGraphService.getAllKnowledgePoints();
    }
    
    // 添加知识点
    @PostMapping("/knowledge-points")
    public boolean addKnowledgePoint(@RequestBody com.example.examsystem.KnowledgePoint knowledgePoint) {
        return knowledgeGraphService.addKnowledgePoint(knowledgePoint);
    }
    
    // 更新知识点
    @PutMapping("/knowledge-points/{kpId}")
    public boolean updateKnowledgePoint(@PathVariable String kpId, @RequestBody com.example.examsystem.KnowledgePoint knowledgePoint) {
        knowledgePoint.setKpId(kpId);
        return knowledgeGraphService.updateKnowledgePoint(knowledgePoint);
    }
    
    // 删除知识点
    @DeleteMapping("/knowledge-points/{kpId}")
    public boolean deleteKnowledgePoint(@PathVariable String kpId) {
        return knowledgeGraphService.deleteKnowledgePoint(kpId);
    }
    
    /**
     * 题库管理
     */
    // 获取推荐题目（用于预览）
    @GetMapping("/questions/recommend")
    public List<Map<String, Object>> recommendQuestions(@RequestParam Long userId, @RequestParam int limit) {
        return questionService.recommendQuestions(userId, limit);
    }
    
    /**
     * 学习路径管理
     */
    // 获取所有学习路径
    @GetMapping("/learning-paths")
    public List<com.example.examsystem.entity.LearningPath> getAllLearningPaths() {
        // 这里需要实现获取所有学习路径的方法
        return null;
    }
    
    /**
     * 系统监控
     */
    // 获取系统状态
    @GetMapping("/system/status")
    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "running");
        status.put("timestamp", System.currentTimeMillis());
        return status;
    }
}