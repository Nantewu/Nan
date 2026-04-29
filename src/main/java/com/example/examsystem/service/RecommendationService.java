package com.example.examsystem.service;

import com.example.examsystem.KnowledgePoint;
import com.example.examsystem.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    
    @Autowired
    private KnowledgeGraphService knowledgeGraphService;
    
    /**
     * 基于混合推荐算法的个性化内容推荐
     */
    public List<KnowledgePoint> recommendContent(User user, int limit) {
        List<KnowledgePoint> recommendations = new ArrayList<>();
        
        // 1. 基于用户历史行为的协同过滤推荐
        List<KnowledgePoint> collaborativeRecommendations = collaborativeFilteringRecommendation(user);
        
        // 2. 基于内容的推荐
        List<KnowledgePoint> contentRecommendations = contentBasedRecommendation(user);
        
        // 3. 基于知识图谱的推荐
        List<KnowledgePoint> graphRecommendations = knowledgeGraphBasedRecommendation(user);
        
        // 4. 混合推荐结果
        recommendations.addAll(collaborativeRecommendations);
        recommendations.addAll(contentRecommendations);
        recommendations.addAll(graphRecommendations);
        
        // 去重并按相关性排序
        Map<Long, KnowledgePoint> uniqueMap = new HashMap<>();
        for (KnowledgePoint point : recommendations) {
            uniqueMap.put(point.getId(), point);
        }
        
        List<KnowledgePoint> finalRecommendations = new ArrayList<>(uniqueMap.values());
        finalRecommendations.sort((a, b) -> {
            int scoreA = calculateRelevanceScore(a, user);
            int scoreB = calculateRelevanceScore(b, user);
            return scoreB - scoreA;
        });
        
        // 返回指定数量的推荐结果
        return finalRecommendations.stream().limit(limit).collect(Collectors.toList());
    }
    
    /**
     * 协同过滤推荐
     */
    private List<KnowledgePoint> collaborativeFilteringRecommendation(User user) {
        // 这里实现协同过滤推荐算法
        // 例如基于用户相似度或物品相似度的推荐
        return knowledgeGraphService.getKnowledgePointsByModule("行测");
    }
    
    /**
     * 基于内容的推荐
     */
    private List<KnowledgePoint> contentBasedRecommendation(User user) {
        // 这里实现基于内容的推荐算法
        // 例如基于用户历史偏好和知识点内容的匹配
        return knowledgeGraphService.getKnowledgePointsByModule("申论");
    }
    
    /**
     * 基于知识图谱的推荐
     */
    private List<KnowledgePoint> knowledgeGraphBasedRecommendation(User user) {
        // 这里实现基于知识图谱的推荐算法
        // 例如基于知识点之间的关联关系
        return knowledgeGraphService.getKnowledgePointsByModule("行测");
    }
    
    /**
     * 计算知识点与用户的相关性得分
     */
    private int calculateRelevanceScore(KnowledgePoint point, User user) {
        int score = 0;
        
        // 基于难度系数和考查频率
        score += (int)(point.getDifficulty() * 100) * 3;
        score += point.getFrequency() * 2;
        
        // 基于用户目标考试
        if (user.getTargetExam() != null && !user.getTargetExam().equals("未设置")) {
            score += 5;
        }
        
        return score;
    }
    
    /**
     * 基于用户学习进度的推荐
     */
    public List<KnowledgePoint> recommendBasedOnProgress(User user, int limit) {
        // 这里实现基于用户学习进度的推荐
        // 例如推荐用户尚未掌握的知识点
        return knowledgeGraphService.getKnowledgePointsByModule("行测");
    }
    
    /**
     * 基于热门度的推荐
     */
    public List<KnowledgePoint> recommendPopularContent(int limit) {
        // 这里实现基于热门度的推荐
        // 例如推荐被大多数用户学习的知识点
        return knowledgeGraphService.getKnowledgePointsByModule("申论");
    }
}