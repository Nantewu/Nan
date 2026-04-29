package com.example.examsystem.service;

import com.example.examsystem.entity.AnswerRecord;
import com.example.examsystem.entity.StudyLog;
import com.example.examsystem.mapper.AnswerRecordMapper;
import com.example.examsystem.mapper.StudyLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AbilityService {
    
    @Autowired
    private AnswerRecordMapper answerRecordMapper;
    
    @Autowired
    private StudyLogMapper studyLogMapper;
    
    /**
     * 评估用户能力
     */
    @Cacheable(value = "abilityEvaluation", key = "#userId")
    public Map<String, Object> evaluateAbility(Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        // 计算各模块能力值
        Map<String, Double> moduleScores = calculateModuleScores(userId);
        result.put("moduleScores", moduleScores);
        
        // 生成能力雷达图数据
        Map<String, Object> radarData = generateRadarData(moduleScores);
        result.put("radarData", radarData);
        
        // 分析薄弱点
        List<String> weakPoints = identifyWeakPoints(moduleScores);
        result.put("weakPoints", weakPoints);
        
        // 计算模块均衡性
        double balanceScore = calculateBalanceScore(moduleScores);
        result.put("balanceScore", balanceScore);
        
        // 生成能力评估报告
        String report = generateAbilityReport(moduleScores, weakPoints, balanceScore);
        result.put("report", report);
        
        return result;
    }
    
    /**
     * 计算各模块能力值
     */
    private Map<String, Double> calculateModuleScores(Long userId) {
        Map<String, Double> scores = new HashMap<>();
        
        // 模块列表
        String[] modules = {"常识判断", "言语理解", "数量关系", "判断推理", "资料分析", "申论"};
        
        for (String module : modules) {
            // 这里应该根据用户的答题记录和学习日志计算能力值
            // 目前使用模拟数据
            double score = 0.5 + Math.random() * 0.5; // 0.5-1.0之间的随机值
            scores.put(module, Math.round(score * 100) / 100.0);
        }
        
        return scores;
    }
    
    /**
     * 生成能力雷达图数据
     */
    private Map<String, Object> generateRadarData(Map<String, Double> moduleScores) {
        Map<String, Object> radarData = new HashMap<>();
        
        // 模块名称
        String[] modules = {"常识判断", "言语理解", "数量关系", "判断推理", "资料分析", "申论"};
        radarData.put("modules", modules);
        
        // 能力值
        double[] values = new double[modules.length];
        for (int i = 0; i < modules.length; i++) {
            values[i] = moduleScores.getOrDefault(modules[i], 0.0);
        }
        radarData.put("values", values);
        
        return radarData;
    }
    
    /**
     * 识别薄弱点
     */
    private List<String> identifyWeakPoints(Map<String, Double> moduleScores) {
        List<String> weakPoints = new java.util.ArrayList<>();
        
        for (Map.Entry<String, Double> entry : moduleScores.entrySet()) {
            if (entry.getValue() < 0.6) {
                weakPoints.add(entry.getKey());
            }
        }
        
        return weakPoints;
    }
    
    /**
     * 计算模块均衡性
     */
    private double calculateBalanceScore(Map<String, Double> moduleScores) {
        // 计算标准差
        double sum = 0.0;
        double mean = 0.0;
        double variance = 0.0;
        
        for (Double score : moduleScores.values()) {
            sum += score;
        }
        mean = sum / moduleScores.size();
        
        for (Double score : moduleScores.values()) {
            variance += Math.pow(score - mean, 2);
        }
        variance /= moduleScores.size();
        double stdDev = Math.sqrt(variance);
        
        // 均衡性得分 = 1 - 标准差
        double balanceScore = 1 - stdDev;
        return Math.max(0, Math.min(1, balanceScore));
    }
    
    /**
     * 生成能力评估报告
     */
    private String generateAbilityReport(Map<String, Double> moduleScores, List<String> weakPoints, double balanceScore) {
        StringBuilder report = new StringBuilder();
        
        report.append("# 能力评估报告\n\n");
        report.append("## 各模块能力值\n");
        for (Map.Entry<String, Double> entry : moduleScores.entrySet()) {
            report.append(String.format("- %s: %.2f\n", entry.getKey(), entry.getValue()));
        }
        
        report.append("\n## 薄弱点\n");
        if (weakPoints.isEmpty()) {
            report.append("无明显薄弱点\n");
        } else {
            for (String point : weakPoints) {
                report.append(String.format("- %s\n", point));
            }
        }
        
        report.append("\n## 模块均衡性\n");
        report.append(String.format("均衡性得分: %.2f\n", balanceScore));
        
        if (balanceScore > 0.8) {
            report.append("模块发展较为均衡\n");
        } else if (balanceScore > 0.5) {
            report.append("模块发展基本均衡\n");
        } else {
            report.append("模块发展不均衡，需要加强薄弱模块的学习\n");
        }
        
        return report.toString();
    }
}