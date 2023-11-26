package com.company.cybersecurity;

import com.company.cybersecurity.config.StorageProperties;
import com.company.cybersecurity.utils.SHAUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class CybersecurityApplication {
    public static void main(String[] args) {
        SpringApplication.run(CybersecurityApplication.class, args);
    }
}
