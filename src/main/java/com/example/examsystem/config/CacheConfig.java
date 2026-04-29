package com.example.examsystem.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    // 缓存配置类，启用Spring Cache支持
    // 可以根据需要配置具体的缓存实现，如Redis、EhCache等
}