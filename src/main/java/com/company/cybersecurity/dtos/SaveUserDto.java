package com.company.cybersecurity.dtos;

import com.company.cybersecurity.models.User;
import lombok.Data;

@Data
public class SaveUserDto {
    private String username;
    private String password;
    private String confirmPassword;

    public static User toUser(SaveUserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        return user;
    }
}
