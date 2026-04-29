package com.example.examsystem;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@Node("KnowledgePoint")
public class KnowledgePoint {
    @Id
    @GeneratedValue
    private Long id;

    @Property("kp_id")
    private String kpId; // 唯一标识
    
    @Property("name")
    private String name; // 知识点名称
    
    @Property("module")
    private String module; // 所属模块：常识判断、言语理解、数量关系、判断推理、资料分析、申论
    
    @Property("difficulty")
    private double difficulty; // 难度系数，0-1
    
    @Property("frequency")
    private int frequency; // 考查频次
    
    @Property("core_concept")
    private String coreConcept; // 核心概念
    
    @Property("suggested_time")
    private int suggestedTime; // 建议掌握时长（分钟）
    
    @Property("mastery_score")
    private double masteryScore; // 掌握度阈值

    @Relationship(type = "Predecessor", direction = Relationship.Direction.OUTGOING)
    private List<KnowledgePoint> predecessors;
    
    @Relationship(type = "Successor", direction = Relationship.Direction.OUTGOING)
    private List<KnowledgePoint> successors;
    
    @Relationship(type = "CrossModule", direction = Relationship.Direction.OUTGOING)
    private List<KnowledgePoint> crossModuleRelations;
}