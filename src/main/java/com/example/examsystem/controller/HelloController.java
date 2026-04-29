package com.example.examsystem.controller; // 确保包名和你创建的路径一致

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "报告！考公智能学习系统指挥部已就绪，JDK 21 动力充足！";
    }
}