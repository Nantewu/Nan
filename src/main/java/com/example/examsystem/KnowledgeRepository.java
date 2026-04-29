package com.example.examsystem;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public interface KnowledgeRepository extends Neo4jRepository<KnowledgePoint, Long> {
    // 根据模块查询知识点
    List<KnowledgePoint> findByModule(String module);
    
    // 根据知识点ID查询
    @Query("MATCH (k:KnowledgePoint) WHERE k.kp_id = $kpId RETURN k")
    KnowledgePoint findByKpId(String kpId);
    
    // 查询知识点的前驱知识点
    @Query("MATCH (k1:KnowledgePoint)-[:Predecessor]->(k2:KnowledgePoint) WHERE k1.kp_id = $kpId RETURN k2")
    List<KnowledgePoint> findPredecessors(String kpId);
    
    // 查询知识点的后继知识点
    @Query("MATCH (k1:KnowledgePoint)-[:Successor]->(k2:KnowledgePoint) WHERE k1.kp_id = $kpId RETURN k2")
    List<KnowledgePoint> findSuccessors(String kpId);
    
    // 查询知识点的跨模块关联
    @Query("MATCH (k1:KnowledgePoint)-[:CrossModule]->(k2:KnowledgePoint) WHERE k1.kp_id = $kpId RETURN k2")
    List<KnowledgePoint> findCrossModuleRelations(String kpId);
    
    // 查询多跳路径
    @Query("MATCH path = (k1:KnowledgePoint)-[*1..3]->(k2:KnowledgePoint) WHERE k1.kp_id = $startKpId AND k2.kp_id = $endKpId RETURN path")
    List<Object> findPathBetweenPoints(String startKpId, String endKpId);
    
    // 按模块/难度筛选知识点子图
    @Query("MATCH (k:KnowledgePoint) WHERE k.module = $module AND k.difficulty >= $minDifficulty AND k.difficulty <= $maxDifficulty RETURN k")
    List<KnowledgePoint> findByModuleAndDifficultyRange(String module, double minDifficulty, double maxDifficulty);
    
    // 按关系权重排序返回最优学习路径
    @Query("MATCH (k1:KnowledgePoint)-[r:Predecessor|Successor|CrossModule]->(k2:KnowledgePoint) WHERE k1.kp_id = $kpId RETURN k2, r.weight ORDER BY r.weight DESC")
    List<Map<String, Object>> findWeightedRelations(String kpId);
    
    // 根据知识点ID删除知识点
    @Query("MATCH (k:KnowledgePoint) WHERE k.kp_id = $kpId DETACH DELETE k")
    void deleteByKpId(String kpId);
}