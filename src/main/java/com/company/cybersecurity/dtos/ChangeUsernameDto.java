package com.company.cybersecurity.dtos;

import lombok.Data;

@Data
public class ChangeUsernameDto {
    private String email;
    private String password;
    private String newUsername;
}
