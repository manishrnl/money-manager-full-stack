package com.example.moneymanager.controller;

import com.example.moneymanager.dto.CategoryDto;
import com.example.moneymanager.services.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> saveCategories(@RequestBody CategoryDto categoryDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.saveCategory(categoryDto));
    }

    @GetMapping()
    public ResponseEntity<List<CategoryDto>> getCategoriesForCurrentUser() {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategoriesForCurrentUser());
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategoriesForCurrentUser(@RequestBody CategoryDto categoryDto ,@PathVariable Long categoryId) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.updateCategoriesForCurrentUser(categoryDto,categoryId));
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDto>> getCategoryByTypeAndCurrentUser(@PathVariable String type) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategoryByTypeAndCurrentUser(type));
    }
}
