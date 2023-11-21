package com.company.cybersecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.io.File;

@SpringBootApplication
@EnableAspectJAutoProxy
public class DeaneryApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(DeaneryApplication.class, args);
    }

}
