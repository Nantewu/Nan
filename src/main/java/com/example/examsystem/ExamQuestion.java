package com.example.examsystem;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Relationship;
import java.util.List;

@Node("ExamQuestion")
public class ExamQuestion {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String options;
    private String answer;
    private String analysis;

    // 题目 → 知识点 的关系
    @Relationship(type = "TESTS_KNOWLEDGE", direction = Relationship.Direction.OUTGOING)
    private List<KnowledgePoint> knowledgePoints;

    // Getter + Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public String getAnalysis() { return analysis; }
    public void setAnalysis(String analysis) { this.analysis = analysis; }
    public List<KnowledgePoint> getKnowledgePoints() { return knowledgePoints; }
    public void setKnowledgePoints(List<KnowledgePoint> knowledgePoints) { this.knowledgePoints = knowledgePoints; }
}