package com.company.cybersecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class CybersecurityApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(CybersecurityApplication.class, args);
    }

}
