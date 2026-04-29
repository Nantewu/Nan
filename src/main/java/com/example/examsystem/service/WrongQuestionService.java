package com.example.examsystem.service;

import com.example.examsystem.entity.WrongQuestion;
import com.example.examsystem.mapper.WrongQuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WrongQuestionService {
    
    @Autowired
    private WrongQuestionMapper wrongQuestionMapper;
    
    @Autowired
    private KnowledgeGraphService knowledgeGraphService;
    
    /**
     * 添加入错题本
     */
    public boolean addWrongQuestion(WrongQuestion wrongQuestion) {
        wrongQuestion.setCreateTime(java.time.LocalDateTime.now());
        wrongQuestion.setLastReviewTime(java.time.LocalDateTime.now());
        wrongQuestion.setReviewCount(0);
        
        // 进行语义解析和根因分析
        Map<String, Object> analysis = analyzeWrongQuestion(wrongQuestion);
        wrongQuestion.setRootCause((String) analysis.get("rootCause"));
        wrongQuestion.setRelatedKnowledgePoints((String) analysis.get("relatedKnowledgePoints"));
        wrongQuestion.setRemediationSuggestion((String) analysis.get("remediationSuggestion"));
        wrongQuestion.setSemanticAnalysis((String) analysis.get("semanticAnalysis"));
        
        int result = wrongQuestionMapper.insert(wrongQuestion);
        return result > 0;
    }
    
    /**
     * 获取用户的错题列表
     */
    @Cacheable(value = "userWrongQuestions", key = "#userId")
    public List<WrongQuestion> getUserWrongQuestions(Long userId) {
        return wrongQuestionMapper.selectByUserId(userId);
    }
    
    /**
     * 深度错题分析
     */
    @Cacheable(value = "wrongQuestionAnalysis", key = "#userId")
    public Map<String, Object> analyzeWrongQuestions(Long userId) {
        List<WrongQuestion> wrongQuestions = wrongQuestionMapper.selectByUserId(userId);
        Map<String, Object> analysisResult = new HashMap<>();
        
        // 1. 统计分析
        Map<String, Integer> knowledgePointCount = new HashMap<>();
        Map<String, Integer> moduleCount = new HashMap<>();
        
        for (WrongQuestion question : wrongQuestions) {
            // 统计知识点错误次数
            knowledgePointCount.put(question.getKnowledgePoint(), 
                knowledgePointCount.getOrDefault(question.getKnowledgePoint(), 0) + 1);
            
            // 统计模块错误次数
            moduleCount.put(question.getModule(), 
                moduleCount.getOrDefault(question.getModule(), 0) + 1);
        }
        
        analysisResult.put("totalWrongCount", wrongQuestions.size());
        analysisResult.put("knowledgePointAnalysis", knowledgePointCount);
        analysisResult.put("moduleAnalysis", moduleCount);
        
        // 2. 错误归因分析
        List<Map<String, Object>> errorAnalysis = new ArrayList<>();
        for (WrongQuestion question : wrongQuestions) {
            Map<String, Object> errorItem = new HashMap<>();
            errorItem.put("questionId", question.getId());
            errorItem.put("questionContent", question.getQuestionContent());
            errorItem.put("userAnswer", question.getUserAnswer());
            errorItem.put("correctAnswer", question.getCorrectAnswer());
            errorItem.put("knowledgePoint", question.getKnowledgePoint());
            errorItem.put("errorReason", question.getRootCause());
            errorAnalysis.add(errorItem);
        }
        analysisResult.put("errorAnalysis", errorAnalysis);
        
        // 3. 知识点关联分析
        Map<String, List<String>> knowledgeRelations = analyzeKnowledgeRelations(wrongQuestions);
        analysisResult.put("knowledgeRelations", knowledgeRelations);
        
        // 4. 推荐练习
        List<String> recommendedExercises = recommendExercises(wrongQuestions);
        analysisResult.put("recommendedExercises", recommendedExercises);
        
        return analysisResult;
    }
    
    /**
     * 分析错题根因
     */
    public Map<String, Object> analyzeWrongQuestion(WrongQuestion wrongQuestion) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 语义解析
        String semanticAnalysis = semanticAnalysis(wrongQuestion.getQuestionContent(), wrongQuestion.getUserAnswer(), wrongQuestion.getCorrectAnswer());
        result.put("semanticAnalysis", semanticAnalysis);
        
        // 2. 知识点匹配
        List<String> relatedKnowledgePoints = matchKnowledgePoints(wrongQuestion.getQuestionContent(), wrongQuestion.getModule());
        result.put("relatedKnowledgePoints", String.join(", ", relatedKnowledgePoints));
        
        // 3. 根因分析
        String rootCause = analyzeRootCause(wrongQuestion, relatedKnowledgePoints);
        result.put("rootCause", rootCause);
        
        // 4. 生成补救建议
        String remediationSuggestion = generateRemediationSuggestion(relatedKnowledgePoints, rootCause);
        result.put("remediationSuggestion", remediationSuggestion);
        
        return result;
    }
    
    /**
     * 语义解析
     */
    private String semanticAnalysis(String questionContent, String userAnswer, String correctAnswer) {
        // 这里可以实现语义解析逻辑
        // 目前使用简单的规则匹配
        if (userAnswer == null || userAnswer.isEmpty()) {
            return "未作答，可能是对知识点不熟悉";
        } else if (userAnswer.equals(correctAnswer)) {
            return "答案正确，可能是其他原因导致标记为错题";
        } else {
            return "答案错误，需要分析知识点掌握情况";
        }
    }
    
    /**
     * 知识点匹配
     */
    private List<String> matchKnowledgePoints(String questionContent, String module) {
        // 这里可以实现知识点匹配逻辑
        // 目前返回模拟数据
        List<String> points = new ArrayList<>();
        if (module.equals("数量关系")) {
            points.add("比例运算");
            points.add("行程问题");
        } else if (module.equals("言语理解")) {
            points.add("逻辑填空");
            points.add("阅读理解");
        } else if (module.equals("判断推理")) {
            points.add("图形推理");
            points.add("逻辑判断");
        } else if (module.equals("资料分析")) {
            points.add("增长率计算");
            points.add("比重分析");
        } else if (module.equals("常识判断")) {
            points.add("政治常识");
            points.add("历史常识");
        } else if (module.equals("申论")) {
            points.add("归纳概括");
            points.add("议论文写作");
        } else {
            points.add("基础知识");
        }
        return points;
    }
    
    /**
     * 根因分析
     */
    private String analyzeRootCause(WrongQuestion wrongQuestion, List<String> relatedKnowledgePoints) {
        // 这里可以实现根因分析逻辑
        // 目前返回模拟数据
        if (relatedKnowledgePoints.size() > 1) {
            return "知识点关联复杂，可能存在知识断点";
        } else {
            return "单个知识点掌握不牢固";
        }
    }
    
    /**
     * 生成补救建议
     */
    private String generateRemediationSuggestion(List<String> relatedKnowledgePoints, String rootCause) {
        // 这里可以实现补救建议生成逻辑
        // 目前返回模拟数据
        StringBuilder suggestion = new StringBuilder();
        suggestion.append("建议：\n");
        for (String point : relatedKnowledgePoints) {
            suggestion.append("- 复习知识点：").append(point).append("\n");
        }
        suggestion.append("- 做相关练习题，加强巩固\n");
        suggestion.append("- 查看知识点的前驱知识点，确保基础牢固");
        return suggestion.toString();
    }
    
    /**
     * 分析知识点关联
     */
    private Map<String, List<String>> analyzeKnowledgeRelations(List<WrongQuestion> wrongQuestions) {
        Map<String, List<String>> relations = new HashMap<>();
        
        // 提取所有涉及的知识点
        Set<String> knowledgePoints = wrongQuestions.stream()
            .map(WrongQuestion::getKnowledgePoint)
            .collect(Collectors.toSet());
        
        // 分析知识点之间的关联
        for (String point : knowledgePoints) {
            // 这里可以利用知识图谱查询相关知识点
            relations.put(point, new ArrayList<>());
        }
        
        return relations;
    }
    
    /**
     * 推荐针对性练习
     */
    private List<String> recommendExercises(List<WrongQuestion> wrongQuestions) {
        List<String> recommendations = new ArrayList<>();
        
        // 基于错题的知识点推荐练习
        Map<String, Integer> knowledgePointCount = wrongQuestions.stream()
            .collect(Collectors.groupingBy(WrongQuestion::getKnowledgePoint, Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .collect(Collectors.toMap(
                Map.Entry::getKey, 
                e -> e.getValue().intValue(), 
                (e1, e2) -> e1, 
                LinkedHashMap::new
            ));
        
        // 为错误最多的知识点推荐练习
        for (Map.Entry<String, Integer> entry : knowledgePointCount.entrySet()) {
            recommendations.add("针对" + entry.getKey() + "的专项练习");
        }
        
        return recommendations;
    }
    
    /**
     * 复习错题
     */
    public boolean reviewWrongQuestion(Long id) {
        WrongQuestion question = wrongQuestionMapper.selectById(id);
        if (question != null) {
            question.setLastReviewTime(java.time.LocalDateTime.now());
            question.setReviewCount(question.getReviewCount() + 1);
            int result = wrongQuestionMapper.updateById(question);
            return result > 0;
        }
        return false;
    }
    
    /**
     * 获取错题根因分析报告
     */
    public Map<String, Object> getRootCauseAnalysis(Long wrongQuestionId) {
        WrongQuestion wrongQuestion = wrongQuestionMapper.selectById(wrongQuestionId);
        if (wrongQuestion == null) {
            return null;
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("wrongQuestion", wrongQuestion);
        result.put("analysis", analyzeWrongQuestion(wrongQuestion));
        
        // 获取相关知识点的详细信息
        List<Map<String, Object>> relatedPoints = new ArrayList<>();
        for (String kpName : wrongQuestion.getRelatedKnowledgePoints().split(", ")) {
            // 这里应该从知识图谱中获取知识点详情
            Map<String, Object> pointInfo = new HashMap<>();
            pointInfo.put("name", kpName);
            pointInfo.put("module", wrongQuestion.getModule());
            relatedPoints.add(pointInfo);
        }
        result.put("relatedPoints", relatedPoints);
        
        return result;
    }
}