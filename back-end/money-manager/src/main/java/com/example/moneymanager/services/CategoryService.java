package com.example.moneymanager.services;

import com.example.moneymanager.dto.CategoryDto;
import com.example.moneymanager.entity.CategoryEntity;
import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.exceptions.BadRequestException;
import com.example.moneymanager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;
    private final ModelMapper modelMapper;

    public CategoryDto saveCategory(CategoryDto categoryDto) {
        // 1. Get the current profile (Properly handle empty Optional)
        ProfileEntity profile = profileService.getCurrentProfile();


        // 2. Check for duplicates
        if (categoryRepository.existsByNameAndProfileId(categoryDto.getName(), profile.getId())) {
            throw new BadRequestException("Category with name " + categoryDto.getName() + " already exists");
        }

        // 3. Map DTO to Entity (NOT Profile to Entity!)
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(categoryDto.getName());
        categoryEntity.setIcon(categoryDto.getIcon());
        categoryEntity.setType(categoryDto.getType());
        // 4. Ensure ID is null so Hibernate knows it's a NEW record (Insert)
        categoryEntity.setId(null);
        categoryEntity.setProfile(profile);

        CategoryEntity savedCategory = categoryRepository.save(categoryEntity);

        return modelMapper.map(savedCategory, CategoryDto.class);
    }

    public List<CategoryDto> getCategoriesForCurrentUser() {
        ProfileEntity currentUser = profileService.getCurrentProfile();
        List<CategoryEntity> categoryEntity = categoryRepository.findByProfileId(currentUser.getId());

        return categoryEntity.stream()
                .map(data -> modelMapper.map(data, CategoryDto.class))
                .collect(Collectors.toList());
    }

    public List<CategoryDto> getCategoryByTypeAndCurrentUser(String type) {
        ProfileEntity currentUser = profileService.getCurrentProfile();
        List<CategoryEntity> categoryEntity = categoryRepository.findByTypeAndProfileId(type, currentUser.getId());


        return categoryEntity.stream()
                .map(data -> modelMapper.map(data, CategoryDto.class))
                .collect(Collectors.toList());
    }

    public CategoryDto updateCategoriesForCurrentUser(CategoryDto categoryDto, Long categoryId) {
        ProfileEntity currentUser = profileService.getCurrentProfile();
        CategoryEntity categoryEntity = categoryRepository.findByIdAndProfileId(categoryId, currentUser.getId());

        categoryEntity.setName(categoryDto.getName());
        categoryEntity.setType(categoryDto.getType());
        categoryEntity.setIcon(categoryDto.getIcon());
        categoryEntity = categoryRepository.save(categoryEntity);
        return modelMapper.map(categoryEntity, CategoryDto.class);

    }
}
