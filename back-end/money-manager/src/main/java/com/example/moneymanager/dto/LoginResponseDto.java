package com.example.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String accessToken;
    private String refreshToken;
}
