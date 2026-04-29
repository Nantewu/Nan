package com.example.examsystem;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AbilityRepository extends Neo4jRepository<Ability, Long> {
}