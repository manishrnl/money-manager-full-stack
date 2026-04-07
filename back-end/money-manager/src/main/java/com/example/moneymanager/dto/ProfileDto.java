package com.example.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Getter
@Setter

public class ProfileDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String email;
    private String fullName;
    private String password;
    private String profileImageUrl;
    private String activationToken;
}