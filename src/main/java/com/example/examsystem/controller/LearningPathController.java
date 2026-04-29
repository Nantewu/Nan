package com.example.examsystem.controller;

import com.example.examsystem.entity.LearningPath;
import com.example.examsystem.service.LearningPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/learning-path")
public class LearningPathController {
    
    @Autowired
    private LearningPathService learningPathService;
    
    /**
     * 获取用户的学习路径列表
     */
    @GetMapping("/user/{userId}")
    public List<LearningPath> getUserLearningPaths(@PathVariable Long userId) {
        return learningPathService.getUserLearningPaths(userId);
    }
    
    /**
     * 获取当前学习路径
     */
    @GetMapping("/current/{userId}")
    public LearningPath getCurrentLearningPath(@PathVariable Long userId) {
        return learningPathService.getCurrentLearningPath(userId);
    }
    
    /**
     * 获取学习路径详情
     */
    @GetMapping("/detail/{pathId}")
    public LearningPath getLearningPathById(@PathVariable Long pathId) {
        return learningPathService.getLearningPathById(pathId);
    }
    
    /**
     * 生成自适应学习路径
     */
    @PostMapping("/generate")
    public LearningPath generateAdaptivePath(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        String startKpId = (String) request.get("startKpId");
        String endKpId = (String) request.get("endKpId");
        String targetExam = (String) request.get("targetExam");
        
        return learningPathService.generateAdaptivePath(userId, startKpId, endKpId, targetExam);
    }
    
    /**
     * 动态更新学习路径
     */
    @PutMapping("/update/{pathId}")
    public LearningPath updateLearningPath(@PathVariable Long pathId, @RequestParam Long userId) {
        return learningPathService.updateLearningPath(pathId, userId);
    }
    
    /**
     * 更新学习进度
     */
    @PutMapping("/progress/{pathId}")
    public boolean updateLearningProgress(@PathVariable Long pathId, @RequestParam int progress) {
        return learningPathService.updateLearningProgress(pathId, progress);
    }
    
    /**
     * 分析学习路径效果
     */
    @GetMapping("/analyze/{pathId}")
    public Map<String, Object> analyzePathEffectiveness(@PathVariable Long pathId) {
        return learningPathService.analyzePathEffectiveness(pathId);
    }
}