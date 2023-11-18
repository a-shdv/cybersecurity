package com.company.cybersecurity.dtos;

import lombok.Data;

@Data
public class SaveUserDto {
    private String username;
    private String password;
    private String confirmPassword;
}
