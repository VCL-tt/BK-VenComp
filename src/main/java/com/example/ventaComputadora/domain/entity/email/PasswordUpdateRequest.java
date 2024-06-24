package com.example.ventaComputadora.domain.entity.email;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordUpdateRequest {
    private String email;
    private String token;
    private String newPassword;
}