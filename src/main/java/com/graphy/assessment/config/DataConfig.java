package com.graphy.assessment.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
 
@Configuration
@EnableJpaRepositories(basePackages = "com.graphy.assessment.repository")
public class DataConfig {
}