package com.example.examsystem.service;

import com.example.examsystem.entity.AnswerRecord;
import com.example.examsystem.mapper.AnswerRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.*;
import java.time.LocalDateTime;
import java.time.Duration;

@Service
public class QuestionService {
    
    @Autowired
    private AnswerRecordMapper answerRecordMapper;
    
    @Autowired
    private KnowledgeGraphService knowledgeGraphService;
    
    /**
     * 获取用户的答题记录
     */
    public List<AnswerRecord> getUserAnswerRecords(Long userId) {
        return answerRecordMapper.selectByUserId(userId);
    }
    
    /**
     * 提交答案记录
     */
    public boolean submitAnswer(AnswerRecord answerRecord) {
        answerRecord.setAnswerTime(LocalDateTime.now());
        return answerRecordMapper.insert(answerRecord) > 0;
    }
    
    /**
     * 生成个性化题组推荐
     */
    @Cacheable(value = "questionRecommendations", key = "#userId + '_' + #limit")
    public List<Map<String, Object>> recommendQuestions(Long userId, int limit) {
        // 1. 获取用户答题历史
        List<AnswerRecord> userRecords = answerRecordMapper.selectByUserId(userId);
        
        // 2. 获取所有用户的答题记录（用于协同过滤）
        List<AnswerRecord> allRecords = answerRecordMapper.selectList(null);
        
        // 3. 计算用户相似度
        Map<Long, Double> userSimilarity = calculateUserSimilarity(userId, userRecords, allRecords);
        
        // 4. 基于相似度推荐题目
        List<Map<String, Object>> recommendedQuestions = new ArrayList<>();
        
        // 5. 结合艾宾浩斯遗忘曲线调整推荐权重
        Map<String, Double> forgettingWeights = calculateForgettingWeights(userRecords);
        
        // 6. 生成推荐题组
        recommendedQuestions = generateRecommendedQuestions(userId, userSimilarity, forgettingWeights, limit);
        
        return recommendedQuestions;
    }
    
    /**
     * 计算用户相似度（余弦相似度）
     */
    private Map<Long, Double> calculateUserSimilarity(Long targetUserId, List<AnswerRecord> targetUserRecords, List<AnswerRecord> allRecords) {
        Map<Long, Map<Long, Integer>> userQuestionMap = new HashMap<>();
        
        // 构建用户-题目矩阵
        for (AnswerRecord record : allRecords) {
            Long userId = record.getUserId();
            Long questionId = record.getQuestionId();
            int score = record.getIsCorrect() ? 1 : 0;
            
            userQuestionMap.computeIfAbsent(userId, k -> new HashMap<>()).put(questionId, score);
        }
        
        // 计算目标用户与其他用户的相似度
        Map<Long, Double> similarityMap = new HashMap<>();
        Map<Long, Integer> targetUserMap = userQuestionMap.get(targetUserId);
        
        if (targetUserMap == null) {
            return similarityMap;
        }
        
        for (Map.Entry<Long, Map<Long, Integer>> entry : userQuestionMap.entrySet()) {
            Long userId = entry.getKey();
            if (userId.equals(targetUserId)) {
                continue;
            }
            
            Map<Long, Integer> userMap = entry.getValue();
            double similarity = calculateCosineSimilarity(targetUserMap, userMap);
            if (similarity > 0) {
                similarityMap.put(userId, similarity);
            }
        }
        
        return similarityMap;
    }
    
    /**
     * 计算余弦相似度
     */
    private double calculateCosineSimilarity(Map<Long, Integer> vector1, Map<Long, Integer> vector2) {
        Set<Long> commonQuestions = new HashSet<>(vector1.keySet());
        commonQuestions.retainAll(vector2.keySet());
        
        if (commonQuestions.isEmpty()) {
            return 0;
        }
        
        double dotProduct = 0;
        double norm1 = 0;
        double norm2 = 0;
        
        for (Long questionId : commonQuestions) {
            int score1 = vector1.get(questionId);
            int score2 = vector2.get(questionId);
            dotProduct += score1 * score2;
            norm1 += score1 * score1;
            norm2 += score2 * score2;
        }
        
        if (norm1 == 0 || norm2 == 0) {
            return 0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    /**
     * 计算遗忘权重（基于艾宾浩斯遗忘曲线）
     */
    private Map<String, Double> calculateForgettingWeights(List<AnswerRecord> userRecords) {
        Map<String, Double> weights = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (AnswerRecord record : userRecords) {
            String kpId = record.getKpId().toString();
            Duration duration = Duration.between(record.getAnswerTime(), now);
            long hours = duration.toHours();
            
            // 艾宾浩斯遗忘曲线公式：R(t) = e^(-t/s)，其中s为记忆强度参数
            double forgettingRate = Math.exp(-hours / 24.0); // 假设24小时为一个记忆周期
            weights.put(kpId, forgettingRate);
        }
        
        return weights;
    }
    
    /**
     * 生成推荐题组
     */
    private List<Map<String, Object>> generateRecommendedQuestions(Long userId, Map<Long, Double> userSimilarity, Map<String, Double> forgettingWeights, int limit) {
        List<Map<String, Object>> recommendedQuestions = new ArrayList<>();
        
        // 1. 基于用户相似度推荐
        Map<Long, Double> questionScores = new HashMap<>();
        
        // 模拟题目数据
        List<Map<String, Object>> allQuestions = getMockQuestions();
        
        // 2. 计算每个题目的推荐分数
        for (Map<String, Object> question : allQuestions) {
            Long questionId = (Long) question.get("id");
            String kpId = (String) question.get("kpId");
            double difficulty = (double) question.get("difficulty");
            
            // 基础分数：结合用户相似度和遗忘曲线
            double baseScore = 0;
            
            // 考虑遗忘权重
            if (forgettingWeights.containsKey(kpId)) {
                baseScore += (1 - forgettingWeights.get(kpId)) * 0.5; // 遗忘率越高，推荐权重越大
            }
            
            // 考虑难度适配
            baseScore += (1 - difficulty) * 0.3; // 优先推荐难度适中的题目
            
            // 考虑知识点重要性
            baseScore += 0.2; // 基础权重
            
            questionScores.put(questionId, baseScore);
        }
        
        // 3. 按分数排序并取前N个
        List<Map.Entry<Long, Double>> sortedScores = new ArrayList<>(questionScores.entrySet());
        sortedScores.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        // 4. 构建推荐结果
        for (int i = 0; i < Math.min(limit, sortedScores.size()); i++) {
            Long questionId = sortedScores.get(i).getKey();
            Map<String, Object> question = allQuestions.stream()
                .filter(q -> q.get("id").equals(questionId))
                .findFirst()
                .orElse(null);
            
            if (question != null) {
                recommendedQuestions.add(question);
            }
        }
        
        return recommendedQuestions;
    }
    
    /**
     * 获取模拟题目数据
     */
    private List<Map<String, Object>> getMockQuestions() {
        List<Map<String, Object>> questions = new ArrayList<>();
        
        // 常识判断
        questions.add(Map.of(
            "id", 1L,
            "questionContent", "下列哪个是中国的首都？",
            "options", List.of("北京", "上海", "广州", "深圳"),
            "correctAnswer", "北京",
            "kpId", "kp1",
            "module", "常识判断",
            "difficulty", 0.2,
            "frequency", 5
        ));
        
        // 言语理解
        questions.add(Map.of(
            "id", 2L,
            "questionContent", "下列成语使用正确的是？",
            "options", List.of("画蛇添足", "守株待兔", "亡羊补牢", "掩耳盗铃"),
            "correctAnswer", "亡羊补牢",
            "kpId", "kp2",
            "module", "言语理解",
            "difficulty", 0.4,
            "frequency", 4
        ));
        
        // 数量关系
        questions.add(Map.of(
            "id", 3L,
            "questionContent", "1+2+3+...+100=?",
            "options", List.of("5050", "5000", "5100", "4950"),
            "correctAnswer", "5050",
            "kpId", "kp3",
            "module", "数量关系",
            "difficulty", 0.3,
            "frequency", 5
        ));
        
        // 判断推理
        questions.add(Map.of(
            "id", 4L,
            "questionContent", "如果所有的猫都是哺乳动物，那么白猫是哺乳动物吗？",
            "options", List.of("是", "否", "不确定", "无法判断"),
            "correctAnswer", "是",
            "kpId", "kp4",
            "module", "判断推理",
            "difficulty", 0.3,
            "frequency", 4
        ));
        
        // 资料分析
        questions.add(Map.of(
            "id", 5L,
            "questionContent", "根据图表，2023年GDP增长率是多少？",
            "options", List.of("5.2%", "5.5%", "5.8%", "6.0%"),
            "correctAnswer", "5.2%",
            "kpId", "kp5",
            "module", "资料分析",
            "difficulty", 0.5,
            "frequency", 4
        ));
        
        // 申论
        questions.add(Map.of(
            "id", 6L,
            "questionContent", "请结合材料，谈谈对乡村振兴的理解。",
            "options", List.of("需要从经济、社会、文化等多方面入手", "主要依靠政府投入", "重点发展农业产业", "以上都对"),
            "correctAnswer", "需要从经济、社会、文化等多方面入手",
            "kpId", "kp6",
            "module", "申论",
            "difficulty", 0.6,
            "frequency", 3
        ));
        
        return questions;
    }
    
    /**
     * 基于知识点推荐题目
     */
    @Cacheable(value = "knowledgePointRecommendations", key = "#kpId + '_' + #limit")
    public List<Map<String, Object>> recommendByKnowledgePoint(String kpId, int limit) {
        List<Map<String, Object>> questions = getMockQuestions();
        List<Map<String, Object>> filteredQuestions = new ArrayList<>();
        
        for (Map<String, Object> question : questions) {
            if (question.get("kpId").equals(kpId)) {
                filteredQuestions.add(question);
            }
        }
        
        // 按难度排序
        filteredQuestions.sort((a, b) -> Double.compare((double) a.get("difficulty"), (double) b.get("difficulty")));
        
        return filteredQuestions.subList(0, Math.min(limit, filteredQuestions.size()));
    }
    
    /**
     * 基于模块推荐题目
     */
    @Cacheable(value = "moduleRecommendations", key = "#module + '_' + #limit")
    public List<Map<String, Object>> recommendByModule(String module, int limit) {
        List<Map<String, Object>> questions = getMockQuestions();
        List<Map<String, Object>> filteredQuestions = new ArrayList<>();
        
        for (Map<String, Object> question : questions) {
            if (question.get("module").equals(module)) {
                filteredQuestions.add(question);
            }
        }
        
        // 按考查频次排序
        filteredQuestions.sort((a, b) -> Integer.compare((int) b.get("frequency"), (int) a.get("frequency")));
        
        return filteredQuestions.subList(0, Math.min(limit, filteredQuestions.size()));
    }
}