package com.company.cybersecurity.dtos;

import com.company.cybersecurity.models.User;
import lombok.Data;

@Data
public class LoginDto {
    private String username;
    private String password;

    public static User toUser (LoginDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        return user;
    }
}
