package com.example.examsystem.service;

import com.example.examsystem.KnowledgePoint;
import com.example.examsystem.KnowledgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class KnowledgeGraphService {
    
    @Autowired
    private KnowledgeRepository knowledgeRepository;
    
    /**
     * 构建知识图谱
     */
    public void buildKnowledgeGraph() {
        // 这里可以实现知识图谱的初始化和构建逻辑
        // 例如从数据库或文件中加载知识点，并建立它们之间的关系
    }
    
    /**
     * 获取指定模块的知识点
     */
    @Cacheable(value = "knowledgePoints", key = "#module")
    public List<KnowledgePoint> getKnowledgePointsByModule(String module) {
        return knowledgeRepository.findByModule(module);
    }
    
    /**
     * 根据知识点ID获取详情
     */
    @Cacheable(value = "knowledgePoint", key = "#kpId")
    public KnowledgePoint getKnowledgePointByKpId(String kpId) {
        return knowledgeRepository.findByKpId(kpId);
    }
    
    /**
     * 获取所有知识点
     */
    @Cacheable(value = "allKnowledgePoints")
    public List<KnowledgePoint> getAllKnowledgePoints() {
        return knowledgeRepository.findAll();
    }
    
    /**
     * 添加知识点
     */
    public boolean addKnowledgePoint(KnowledgePoint knowledgePoint) {
        // 检查知识点是否已存在
        if (knowledgeRepository.findByKpId(knowledgePoint.getKpId()) != null) {
            return false;
        }
        // 保存知识点
        knowledgeRepository.save(knowledgePoint);
        return true;
    }
    
    /**
     * 更新知识点
     */
    public boolean updateKnowledgePoint(KnowledgePoint knowledgePoint) {
        // 检查知识点是否存在
        if (knowledgeRepository.findByKpId(knowledgePoint.getKpId()) == null) {
            return false;
        }
        // 更新知识点
        knowledgeRepository.save(knowledgePoint);
        return true;
    }
    
    /**
     * 删除知识点
     */
    public boolean deleteKnowledgePoint(String kpId) {
        // 检查知识点是否存在
        if (knowledgeRepository.findByKpId(kpId) == null) {
            return false;
        }
        // 删除知识点
        knowledgeRepository.deleteByKpId(kpId);
        return true;
    }
    
    /**
     * 获取知识点的依赖关系
     */
    @Cacheable(value = "knowledgeDependencies", key = "#kpId")
    public Map<String, Object> getKnowledgeDependencies(String kpId) {
        Map<String, Object> dependencies = new HashMap<>();
        dependencies.put("predecessors", knowledgeRepository.findPredecessors(kpId));
        dependencies.put("successors", knowledgeRepository.findSuccessors(kpId));
        dependencies.put("crossModuleRelations", knowledgeRepository.findCrossModuleRelations(kpId));
        return dependencies;
    }
    
    /**
     * 分析知识点的重要性和考查频率
     */
    @Cacheable(value = "knowledgeAnalysis", key = "#kpId")
    public Map<String, Object> analyzeKnowledgePoint(String kpId) {
        if (kpId == null) {
            return null;
        }
        KnowledgePoint point = knowledgeRepository.findByKpId(kpId);
        if (point == null) {
            return null;
        }
        
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("kpId", point.getKpId());
        analysis.put("name", point.getName());
        analysis.put("difficulty", point.getDifficulty());
        analysis.put("frequency", point.getFrequency());
        analysis.put("module", point.getModule());
        analysis.put("coreConcept", point.getCoreConcept());
        analysis.put("suggestedTime", point.getSuggestedTime());
        analysis.put("masteryScore", point.getMasteryScore());
        
        // 计算综合评分
        double score = (point.getDifficulty() * 0.5) + (point.getFrequency() * 0.5);
        analysis.put("score", score);
        
        return analysis;
    }
    
    /**
     * 生成知识点可视化数据
     */
    @Cacheable(value = "graphData", key = "#module")
    public Map<String, Object> generateGraphData(String module) {
        List<KnowledgePoint> points = knowledgeRepository.findByModule(module);
        List<Map<String, Object>> nodes = new ArrayList<>();
        List<Map<String, Object>> edges = new ArrayList<>();
        
        // 生成节点
        for (KnowledgePoint point : points) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", point.getKpId());
            node.put("label", point.getName());
            node.put("module", point.getModule());
            node.put("difficulty", point.getDifficulty());
            node.put("frequency", point.getFrequency());
            nodes.add(node);
            
            // 生成边
            List<KnowledgePoint> predecessors = knowledgeRepository.findPredecessors(point.getKpId());
            for (KnowledgePoint pred : predecessors) {
                Map<String, Object> edge = new HashMap<>();
                edge.put("from", pred.getKpId());
                edge.put("to", point.getKpId());
                edge.put("label", "Predecessor");
                edges.add(edge);
            }
            
            List<KnowledgePoint> successors = knowledgeRepository.findSuccessors(point.getKpId());
            for (KnowledgePoint succ : successors) {
                Map<String, Object> edge = new HashMap<>();
                edge.put("from", point.getKpId());
                edge.put("to", succ.getKpId());
                edge.put("label", "Successor");
                edges.add(edge);
            }
            
            List<KnowledgePoint> crossModule = knowledgeRepository.findCrossModuleRelations(point.getKpId());
            for (KnowledgePoint cross : crossModule) {
                Map<String, Object> edge = new HashMap<>();
                edge.put("from", point.getKpId());
                edge.put("to", cross.getKpId());
                edge.put("label", "CrossModule");
                edges.add(edge);
            }
        }
        
        Map<String, Object> graphData = new HashMap<>();
        graphData.put("nodes", nodes);
        graphData.put("edges", edges);
        return graphData;
    }
    
    /**
     * 生成最优学习路径
     */
    public List<KnowledgePoint> generateOptimalPath(String startKpId, String endKpId) {
        List<Object> paths = knowledgeRepository.findPathBetweenPoints(startKpId, endKpId);
        // 这里可以实现路径优化逻辑
        // 目前返回空列表，需要根据实际情况实现
        return new ArrayList<>();
    }
    
    /**
     * 搜索相关知识点
     */
    public List<KnowledgePoint> searchRelatedKnowledge(String keyword) {
        // 这里可以实现基于关键词的知识点搜索
        // 例如使用全文检索或相似度匹配
        return knowledgeRepository.findAll();
    }
}