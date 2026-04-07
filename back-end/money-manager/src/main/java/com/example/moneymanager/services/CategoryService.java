package com.example.moneymanager.services;

import com.example.moneymanager.dto.CategoryDto;
import com.example.moneymanager.entity.CategoryEntity;
import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.exceptions.BadRequestException;
import com.example.moneymanager.repository.CategoryRepository;
import com.example.moneymanager.repository.ProfileRepository;
import com.example.moneymanager.utils.SentenceCase;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;
    private final ModelMapper modelMapper;
    private final SentenceCase sentenceCase;

    private final String CACHE_NAME = "category";


    @CacheEvict(cacheNames = "categories", key = "#root.target.getCurrentUserId()")
    /* COMMENT: We EVICT the cache instead of using @CachePut.
       Since this method adds a new item, the previously cached List of categories
       is now stale. Evicting it forces the application to fetch the updated
       list from the database on the next 'GET' request.
    */
    public CategoryDto saveCategory(CategoryDto categoryDto) {
        ProfileEntity profile = profileService.getCurrentProfile();

        String categoryName = sentenceCase.convertToSentenceCase(categoryDto.getName());
        String categoryType = categoryDto.getType();
        String categoryIcon = categoryDto.getIcon();

        if (categoryRepository.existsByNameAndTypeAndProfileId(categoryName, categoryType, profile.getId())) {
            throw new BadRequestException("Category with name " + categoryDto.getName() + " " +
                    "And Type " + categoryDto.getType() + " already exists");
        }


        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(categoryName);
        categoryEntity.setIcon(categoryIcon);
        categoryEntity.setType(categoryType.toUpperCase());

        categoryEntity.setId(null);
        categoryEntity.setProfile(profile);

        CategoryEntity savedCategory = categoryRepository.save(categoryEntity);

        return modelMapper.map(savedCategory, CategoryDto.class);
    }

    @Cacheable(cacheNames = "categories", key = "#root.target.getCurrentUserId()")
    /* COMMENT: We use @Cacheable with the User ID as the key.
       Category lists are unique per user. This prevents the app from re-running
       the JOIN/SELECT query every time the user navigates to their dashboard.
    */
    public List<CategoryDto> getCategoriesForCurrentUser() {
        ProfileEntity currentUser = profileService.getCurrentProfile();
        List<CategoryEntity> categoryEntity = categoryRepository.findByProfileId(currentUser.getId());

        return categoryEntity.stream()
                .map(data -> modelMapper.map(data, CategoryDto.class))
                .collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "categories", key = "#root.target.getCurrentUserId() + '_' + #type")
    /* COMMENT: We use a compound key (UserId + Type).
       This ensures that a cached list of "EXPENSE" categories doesn't
       accidentally get returned when the user clicks on "INCOME".
    */
    public List<CategoryDto> getCategoryByTypeAndCurrentUser(String type) {
        ProfileEntity currentUser = profileService.getCurrentProfile();
        List<CategoryEntity> categoryEntity = categoryRepository.findByTypeAndProfileId(type, currentUser.getId());


        return categoryEntity.stream()
                .map(data -> modelMapper.map(data, CategoryDto.class))
                .collect(Collectors.toList());
    }


    @Caching(evict = {
            @CacheEvict(cacheNames = "categories", key = "#root.target.getCurrentUserId()"),
            @CacheEvict(cacheNames = "categories", allEntries = true)
    })

    /* COMMENT: We use @Caching to trigger multiple evictions.
       Updating a category name or icon changes the data inside the "All" list
       and the "By Type" list. Clearing these ensures the UI reflects the
       changes immediately.
    */
    public CategoryDto updateCategoriesForCurrentUser(CategoryDto categoryDto, Long categoryId) {
        ProfileEntity currentUser = profileService.getCurrentProfile();
        CategoryEntity categoryEntity = categoryRepository.findByIdAndProfileId(categoryId, currentUser.getId());

        categoryEntity.setName(sentenceCase.convertToSentenceCase(categoryDto.getName()));
        categoryEntity.setType(categoryDto.getType().toUpperCase());
        categoryEntity.setIcon(categoryDto.getIcon());
        categoryEntity = categoryRepository.save(categoryEntity);
        return modelMapper.map(categoryEntity, CategoryDto.class);

    }



    @CacheEvict(cacheNames = "categories", allEntries = true)

    /* COMMENT: We use 'allEntries = true' for the user's category cache.
       Deleting a record is a destructive action that invalidates all
       filtered views (Income/Expense/All). This is the safest way to
       prevent "ghost" data from appearing in the UI.
    */
    public String deleteCategoryByIdAndProfileId(Long categoryId) {
        ProfileEntity currentUser = profileService.getCurrentProfile();
        CategoryEntity users = categoryRepository.findByIdAndProfileId(categoryId, currentUser.getId());

        if (users == null) return "Category does not exists so cant delete";
        int deletedRows = categoryRepository.deleteByIdAndProfileId(categoryId, currentUser.getId());
        if (deletedRows > 0) return "Category deleted successfully";
        else
            throw new RuntimeException("Could not able to delete category with id : " + categoryId + " for user : " + currentUser.getId());
    }

    public Long getCurrentUserId() {
        return profileService.getCurrentProfile().getId();
    }
}
