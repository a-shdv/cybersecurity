package com.company.cybersecurity.dtos;

import lombok.Data;

@Data
public class ChangePasswordExpiredDto {
    private String email;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
