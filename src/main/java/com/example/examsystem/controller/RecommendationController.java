package com.example.examsystem.controller;

import com.example.examsystem.entity.User;
import com.example.examsystem.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recommendation")
public class RecommendationController {
    
    @Autowired
    private RecommendationService recommendationService;
    
    /**
     * 获取个性化推荐内容
     */
    @PostMapping("/personalized")
    public List<?> getPersonalizedRecommendations(@RequestBody User user, @RequestParam int limit) {
        return recommendationService.recommendContent(user, limit);
    }
    
    /**
     * 基于学习进度的推荐
     */
    @PostMapping("/progress-based")
    public List<?> getProgressBasedRecommendations(@RequestBody User user, @RequestParam int limit) {
        return recommendationService.recommendBasedOnProgress(user, limit);
    }
    
    /**
     * 获取热门推荐内容
     */
    @GetMapping("/popular")
    public List<?> getPopularRecommendations(@RequestParam int limit) {
        return recommendationService.recommendPopularContent(limit);
    }
}