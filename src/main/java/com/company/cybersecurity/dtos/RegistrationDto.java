package com.company.cybersecurity.dtos;

import com.company.cybersecurity.models.User;
import lombok.Data;

@Data
public class RegistrationDto {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;

    public static User toUser (RegistrationDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return user;
    }
}
