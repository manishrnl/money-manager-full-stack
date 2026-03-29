package com.example.moneymanager.dto;

import com.example.moneymanager.entity.ProfileEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CategoryDto {

    private Long id;
    private String name;

    private String type;
    private String icon;
    private ProfileEntity profile;


}

