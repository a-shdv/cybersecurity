package com.company.cybersecurity.dtos;

import com.company.cybersecurity.models.User;
import lombok.Data;

@Data
public class LoginDto {
    private String username;
    private String password;
}