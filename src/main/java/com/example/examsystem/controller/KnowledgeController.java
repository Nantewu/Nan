package com.example.examsystem.controller;

import com.example.examsystem.service.KnowledgeGraphService;
import com.example.examsystem.KnowledgePoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class KnowledgeController {
    
    @Autowired
    private KnowledgeGraphService knowledgeGraphService;
    
    /**
     * 获取指定模块的知识图谱数据
     */
    @GetMapping("/api/knowledge/graph")
    public Map<String, Object> getGraphData(@RequestParam String module) {
        return knowledgeGraphService.generateGraphData(module);
    }
    
    /**
     * 获取知识点详情
     */
    @GetMapping("/api/knowledge/point")
    public KnowledgePoint getKnowledgePoint(@RequestParam String kpId) {
        return knowledgeGraphService.getKnowledgePointByKpId(kpId);
    }
    
    /**
     * 获取知识点依赖关系
     */
    @GetMapping("/api/knowledge/dependencies")
    public Map<String, Object> getDependencies(@RequestParam String kpId) {
        return knowledgeGraphService.getKnowledgeDependencies(kpId);
    }
    
    /**
     * 分析知识点
     */
    @GetMapping("/api/knowledge/analyze")
    public Map<String, Object> analyzeKnowledgePoint(@RequestParam String kpId) {
        return knowledgeGraphService.analyzeKnowledgePoint(kpId);
    }
}