package com.example.moneymanager.config;

import com.example.moneymanager.dto.CategoryDto;
import com.example.moneymanager.dto.ExpenseDto;
import com.example.moneymanager.dto.IncomeDto;
import com.example.moneymanager.entity.CategoryEntity;
import com.example.moneymanager.entity.ExpenseEntity;
import com.example.moneymanager.entity.IncomeEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);

        // Mapping for CategoryEntity -> CategoryDto
        modelMapper.addMappings(new PropertyMap<CategoryEntity, CategoryDto>() {
            @Override
            protected void configure() {
                // Map the profile's ID to profileId, not the whole object
                map().setProfileId(source.getProfile().getId());
            }
        });

        // Mapping for ExpenseEntity -> ExpenseDto
        modelMapper.addMappings(new PropertyMap<ExpenseEntity, ExpenseDto>() {
            @Override
            protected void configure() {
                map().setCategoryId(source.getCategory().getId());
                map().setCategoryName(source.getCategory().getName());
            }
        });

        // Mapping for IncomeEntity -> IncomeDto
        modelMapper.addMappings(new PropertyMap<IncomeEntity, IncomeDto>() {
            @Override
            protected void configure() {
                map().setCategoryId(source.getCategory().getId());
                map().setCategoryName(source.getCategory().getName());
            }
        });

        return modelMapper;
    }
}