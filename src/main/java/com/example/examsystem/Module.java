package com.example.examsystem;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Relationship;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@Node("Module")
public class Module {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    // 模块 -> 知识点的关系
    @JsonIgnoreProperties("module")
    @Relationship(type = "HAS_KP", direction = Relationship.Direction.OUTGOING)
    private List<KnowledgePoint> knowledgePoints;

    public Module() {}
}