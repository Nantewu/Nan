package com.example.examsystem.controller;

import com.example.examsystem.service.AbilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class AbilityController {
    
    @Autowired
    private AbilityService abilityService;
    
    /**
     * 评估用户能力
     */
    @GetMapping("/api/ability/evaluate")
    public Map<String, Object> evaluateAbility(@RequestParam Long userId) {
        return abilityService.evaluateAbility(userId);
    }
}