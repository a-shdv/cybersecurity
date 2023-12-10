package com.company.cybersecurity;

import com.company.cybersecurity.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class CybersecurityApplication {
    public static void main(String[] args) {
        SpringApplication.run(CybersecurityApplication.class, args);
    }
}
