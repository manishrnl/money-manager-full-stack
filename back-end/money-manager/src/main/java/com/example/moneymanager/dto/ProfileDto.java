package com.example.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Getter
@Setter

public class ProfileDto {

    private Long id;
    private String email;
    private String fullName;
    private String password;
    private String profileImageUrl;
    private String activationToken;
}