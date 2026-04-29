package com.example.examsystem;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Relationship;
import java.util.List;

@Node("Subject")
public class Subject {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String description;

    // 科目 → 模块 的关系（如果有的话）
    @Relationship(type = "HAS_MODULE", direction = Relationship.Direction.OUTGOING)
    private List<Module> modules;

    // Getter + Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Module> getModules() { return modules; }
    public void setModules(List<Module> modules) { this.modules = modules; }
}