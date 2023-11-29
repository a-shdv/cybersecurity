package com.company.cybersecurity.dto;

import com.company.cybersecurity.models.User;
import lombok.*;

public record RegistrationDto(
        @Getter
        String username,
        @Getter
        String password,
        @Getter
        String passwordConfirm) {
    public RegistrationDto(String username, String password, String passwordConfirm) {
        this.username = username;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }

    public static User toUser(RegistrationDto dto) {
        return new User(dto.getUsername(), dto.getPassword());
    }
}
