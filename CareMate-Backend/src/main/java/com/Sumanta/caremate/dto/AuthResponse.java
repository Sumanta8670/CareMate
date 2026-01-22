package com.Sumanta.caremate.dto;

import com.Sumanta.caremate.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UserRole role;
    private String email;
    private String message;
}