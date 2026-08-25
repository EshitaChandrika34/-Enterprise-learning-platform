package com.enterpriselearning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.enterpriselearning")
@EntityScan(basePackages = "com.enterpriselearning.entity")
@EnableJpaRepositories(basePackages = "com.enterpriselearning.repository")
public class EnterpriseLearningApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseLearningApplication.class, args);
    }
}
