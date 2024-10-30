package com.kkimleang.authservice;

import com.redis.om.spring.annotations.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@EnableRedisEnhancedRepositories(basePackages = {"com.kkimleang.authservice.repository", "com.kkimleang.authservice.model"})
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
