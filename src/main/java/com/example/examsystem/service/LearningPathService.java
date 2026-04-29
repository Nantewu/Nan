package com.example.examsystem.service;

import com.example.examsystem.entity.LearningPath;
import com.example.examsystem.entity.User;
import com.example.examsystem.mapper.LearningPathMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class LearningPathService {
    
    @Autowired
    private LearningPathMapper learningPathMapper;
    
    @Autowired
    private WrongQuestionService wrongQuestionService;
    
    @Autowired
    private KnowledgeGraphService knowledgeGraphService;
    
    @Autowired
    private AbilityService abilityService;
    
    /**
     * 创建学习路径
     */
    public LearningPath createLearningPath(User user, String pathName, String targetExam, Date targetDate) {
        LearningPath path = new LearningPath();
        path.setUserId(user.getUserId());
        path.setPathName(pathName);
        path.setPathType("系统推荐");
        path.setStatus("进行中");
        path.setProgress(0);
        path.setTargetExam(targetExam);
        path.setTargetDate(java.time.LocalDateTime.from(targetDate.toInstant().atZone(java.time.ZoneId.systemDefault())));
        path.setCreateTime(java.time.LocalDateTime.now());
        path.setLastUpdateTime(java.time.LocalDateTime.now());
        
        // 生成初始学习路径
        String pathDetails = generateInitialPath(user, targetExam, targetDate);
        path.setPathDetails(pathDetails);
        
        learningPathMapper.insert(path);
        return path;
    }
    
    /**
     * 生成初始学习路径
     */
    private String generateInitialPath(User user, String targetExam, Date targetDate) {
        // 这里实现初始学习路径的生成逻辑
        // 基于用户目标和知识图谱结构
        Map<String, Object> pathMap = new HashMap<>();
        pathMap.put("modules", Arrays.asList("行测", "申论"));
        pathMap.put("schedule", new ArrayList<>());
        
        // 转换为JSON字符串
        return "{\"modules\":[\"行测\",\"申论\"],\"schedule\":[]}";
    }
    
    /**
     * 基于强化学习的路径动态调整
     */
    public LearningPath adjustLearningPath(Long pathId, Map<String, Object> feedback) {
        LearningPath path = learningPathMapper.selectById(pathId);
        if (path == null) {
            return null;
        }
        
        // 这里实现基于强化学习的路径调整逻辑
        // 考虑用户反馈、学习进度、错误率等因素
        
        // 更新路径详情
        String adjustedPathDetails = adjustPathBasedOnFeedback(path.getPathDetails(), feedback);
        path.setPathDetails(adjustedPathDetails);
        path.setLastUpdateTime(java.time.LocalDateTime.now());
        
        learningPathMapper.updateById(path);
        return path;
    }
    
    /**
     * 根据反馈调整路径
     */
    private String adjustPathBasedOnFeedback(String currentPathDetails, Map<String, Object> feedback) {
        // 这里实现基于反馈的路径调整逻辑
        // 例如，根据用户的学习速度、错误率等调整学习内容和进度
        return currentPathDetails;
    }
    
    /**
     * 获取用户的学习路径
     */
    public List<LearningPath> getUserLearningPaths(Long userId) {
        return learningPathMapper.selectByUserId(userId);
    }
    
    /**
     * 根据ID获取学习路径
     */
    public LearningPath getLearningPathById(Long id) {
        return learningPathMapper.selectById(id);
    }
    
    /**
     * 获取当前学习路径
     */
    public LearningPath getCurrentLearningPath(Long userId) {
        return learningPathMapper.selectCurrentPath(userId);
    }
    
    /**
     * 更新学习进度
     */
    public boolean updateLearningProgress(Long pathId, int progress) {
        LearningPath path = learningPathMapper.selectById(pathId);
        if (path != null) {
            path.setProgress(progress);
            path.setLastUpdateTime(java.time.LocalDateTime.now());
            
            if (progress >= 100) {
                path.setStatus("已完成");
            }
            
            int result = learningPathMapper.updateById(path);
            return result > 0;
        }
        return false;
    }
    
    /**
     * 推荐学习路径
     */
    public LearningPath recommendLearningPath(User user) {
        // 这里实现基于用户特征和目标的学习路径推荐
        // 考虑用户的基础水平、学习时间、目标考试等因素
        return createLearningPath(user, "推荐学习路径", user.getTargetExam(), new Date());
    }
    
    /**
     * 分析学习路径效果
     */
    public Map<String, Object> analyzePathEffectiveness(Long pathId) {
        LearningPath path = learningPathMapper.selectById(pathId);
        if (path == null) {
            return null;
        }
        
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("pathId", path.getId());
        analysis.put("pathName", path.getPathName());
        analysis.put("progress", path.getProgress());
        analysis.put("status", path.getStatus());
        
        // 分析学习效果
        List<?> wrongQuestions = wrongQuestionService.getUserWrongQuestions(path.getUserId());
        analysis.put("wrongQuestionCount", wrongQuestions.size());
        
        // 计算学习效率
        long daysElapsed = java.time.Duration.between(
            path.getCreateTime(), java.time.LocalDateTime.now()).toDays();
        double efficiency = daysElapsed > 0 ? (double) path.getProgress() / daysElapsed : 0;
        analysis.put("efficiency", efficiency);
        
        return analysis;
    }
    
    /**
     * 生成自适应学习路径
     */
    public LearningPath generateAdaptivePath(Long userId, String startKpId, String endKpId, String targetExam) {
        // 1. 获取用户能力评估
        Map<String, Object> abilityEvaluation = abilityService.evaluateAbility(userId);
        Map<String, Double> moduleScores = (Map<String, Double>) abilityEvaluation.get("moduleScores");
        
        // 2. 获取用户错题记录
        List<com.example.examsystem.entity.WrongQuestion> wrongQuestions = wrongQuestionService.getUserWrongQuestions(userId);
        
        // 3. 生成学习路径
        List<Map<String, Object>> pathNodes = generatePathNodes(startKpId, endKpId, moduleScores, wrongQuestions);
        
        // 4. 计算路径属性
        int estimatedDuration = calculateEstimatedDuration(pathNodes);
        double difficultyLevel = calculateDifficultyLevel(pathNodes);
        
        // 5. 创建学习路径对象
        LearningPath learningPath = new LearningPath();
        learningPath.setUserId(userId);
        learningPath.setPathName("自适应学习路径");
        learningPath.setPathType("系统推荐");
        learningPath.setStatus("进行中");
        learningPath.setCreateTime(java.time.LocalDateTime.now());
        learningPath.setLastUpdateTime(java.time.LocalDateTime.now());
        learningPath.setProgress(0);
        learningPath.setTargetExam(targetExam);
        learningPath.setStartKpId(startKpId);
        learningPath.setEndKpId(endKpId);
        learningPath.setEstimatedDuration(estimatedDuration);
        learningPath.setDifficultyLevel(difficultyLevel);
        learningPath.setOptimizationStrategy("基于能力评估和错题分析的路径优化");
        
        // 6. 存储路径详情
        Map<String, Object> pathDetails = new HashMap<>();
        pathDetails.put("nodes", pathNodes);
        pathDetails.put("estimatedDuration", estimatedDuration);
        pathDetails.put("difficultyLevel", difficultyLevel);
        pathDetails.put("abilityEvaluation", abilityEvaluation);
        
        // 这里应该使用JSON序列化，目前使用字符串模拟
        learningPath.setPathDetails(pathDetails.toString());
        
        // 7. 保存学习路径
        learningPathMapper.insert(learningPath);
        
        return learningPath;
    }
    
    /**
     * 生成路径节点
     */
    private List<Map<String, Object>> generatePathNodes(String startKpId, String endKpId, Map<String, Double> moduleScores, List<com.example.examsystem.entity.WrongQuestion> wrongQuestions) {
        List<Map<String, Object>> nodes = new ArrayList<>();
        
        // 1. 获取起始知识点
        com.example.examsystem.KnowledgePoint startPoint = knowledgeGraphService.getKnowledgePointByKpId(startKpId);
        if (startPoint != null) {
            Map<String, Object> startNode = new HashMap<>();
            startNode.put("kpId", startPoint.getKpId());
            startNode.put("name", startPoint.getName());
            startNode.put("module", startPoint.getModule());
            startNode.put("difficulty", startPoint.getDifficulty());
            startNode.put("suggestedTime", startPoint.getSuggestedTime());
            nodes.add(startNode);
        }
        
        // 2. 生成中间节点（这里使用模拟数据）
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> node = new HashMap<>();
            node.put("kpId", "kp" + i);
            node.put("name", "知识点" + i);
            node.put("module", "数量关系");
            node.put("difficulty", 0.5 + i * 0.1);
            node.put("suggestedTime", 30 + i * 10);
            nodes.add(node);
        }
        
        // 3. 获取目标知识点
        com.example.examsystem.KnowledgePoint endPoint = knowledgeGraphService.getKnowledgePointByKpId(endKpId);
        if (endPoint != null) {
            Map<String, Object> endNode = new HashMap<>();
            endNode.put("kpId", endPoint.getKpId());
            endNode.put("name", endPoint.getName());
            endNode.put("module", endPoint.getModule());
            endNode.put("difficulty", endPoint.getDifficulty());
            endNode.put("suggestedTime", endPoint.getSuggestedTime());
            nodes.add(endNode);
        }
        
        return nodes;
    }
    
    /**
     * 计算预计总时长
     */
    private int calculateEstimatedDuration(List<Map<String, Object>> pathNodes) {
        int totalDuration = 0;
        for (Map<String, Object> node : pathNodes) {
            totalDuration += (int) node.get("suggestedTime");
        }
        return totalDuration;
    }
    
    /**
     * 计算路径难度等级
     */
    private double calculateDifficultyLevel(List<Map<String, Object>> pathNodes) {
        double totalDifficulty = 0;
        for (Map<String, Object> node : pathNodes) {
            totalDifficulty += (double) node.get("difficulty");
        }
        return pathNodes.size() > 0 ? totalDifficulty / pathNodes.size() : 0;
    }
    
    /**
     * 动态更新学习路径
     */
    public LearningPath updateLearningPath(Long pathId, Long userId) {
        LearningPath learningPath = learningPathMapper.selectById(pathId);
        if (learningPath == null) {
            return null;
        }
        
        // 1. 获取最新的用户能力评估
        Map<String, Object> abilityEvaluation = abilityService.evaluateAbility(userId);
        
        // 2. 获取最新的错题记录
        List<com.example.examsystem.entity.WrongQuestion> wrongQuestions = wrongQuestionService.getUserWrongQuestions(userId);
        
        // 3. 重新生成路径
        List<Map<String, Object>> pathNodes = generatePathNodes(
            learningPath.getStartKpId(), 
            learningPath.getEndKpId(), 
            (Map<String, Double>) abilityEvaluation.get("moduleScores"), 
            wrongQuestions
        );
        
        // 4. 更新路径属性
        int estimatedDuration = calculateEstimatedDuration(pathNodes);
        double difficultyLevel = calculateDifficultyLevel(pathNodes);
        
        learningPath.setEstimatedDuration(estimatedDuration);
        learningPath.setDifficultyLevel(difficultyLevel);
        learningPath.setLastUpdateTime(java.time.LocalDateTime.now());
        
        // 5. 更新路径详情
        Map<String, Object> pathDetails = new HashMap<>();
        pathDetails.put("nodes", pathNodes);
        pathDetails.put("estimatedDuration", estimatedDuration);
        pathDetails.put("difficultyLevel", difficultyLevel);
        pathDetails.put("abilityEvaluation", abilityEvaluation);
        
        learningPath.setPathDetails(pathDetails.toString());
        
        // 6. 保存更新
        learningPathMapper.updateById(learningPath);
        
        return learningPath;
    }
}