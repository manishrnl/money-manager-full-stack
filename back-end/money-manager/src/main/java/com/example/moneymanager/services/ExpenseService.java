package com.example.moneymanager.services;

import com.example.moneymanager.dto.ExpenseDto;
import com.example.moneymanager.entity.CategoryEntity;
import com.example.moneymanager.entity.ExpenseEntity;
import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.exceptions.ResourceNotFoundException;
import com.example.moneymanager.repository.CategoryRepository;
import com.example.moneymanager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;


    private static final String CACHE_NAME = "expenses";
    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_NAME, allEntries = true),
            @CacheEvict(cacheNames = DashboardService.DASHBOARD_CACHE, key = "#root.target.getCurrentUserId()")
    })
    public ExpenseDto addExpense(ExpenseDto expenseDto) {
        ProfileEntity currentUser = profileService.getCurrentProfile();

        // Fix: Use getCategoryId() for the lookup error message
        CategoryEntity category = categoryRepository.findById(expenseDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + expenseDto.getCategoryId()));

        // Map DTO to Entity
        ExpenseEntity expenseEntity = modelMapper.map(expenseDto, ExpenseEntity.class);

        // Ensure the ID is not overwritten from DTO and set relations
        expenseEntity.setId(null);
        expenseEntity.setProfile(currentUser);
        expenseEntity.setCategory(category);

        ExpenseEntity savedEntity = expenseRepository.save(expenseEntity);
        return modelMapper.map(savedEntity, ExpenseDto.class);
    }


    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_NAME, allEntries = true),
            @CacheEvict(cacheNames = DashboardService.DASHBOARD_CACHE, key = "#root.target.getCurrentUserId()")
    })
    public void deleteExpense(Long expenseId) {
        ProfileEntity currentUser = profileService.getCurrentProfile();

        ExpenseEntity entity = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with ID: " + expenseId));

        // Security Check: Ensure the expense belongs to the logged-in user
        if (!entity.getProfile().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized: You do not have permission to delete this expense");
        }

        expenseRepository.delete(entity);
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#root.target.getCurrentUserId() + '_monthly'")

        /* COMMENT: Cached with a suffix '_monthly'.
           This is a heavy query (Date Range + Profile ID), so caching
           it prevents unnecessary DB hits during dashboard refreshes. */
    public List<ExpenseDto> expensesForCurrentUser() {
        ProfileEntity currentUser = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        return expenseRepository.findByProfileIdAndDateBetween(currentUser.getId(), startDate, endDate)
                .stream()
                .map(data -> modelMapper.map(data, ExpenseDto.class))
                .collect(Collectors.toList());
    }



    @Cacheable(cacheNames = CACHE_NAME, key = "#root.target.getCurrentUserId() + '_latest'")
        /* COMMENT: The dashboard 'Recent Activity' usually calls this.
           Caching it makes the landing page feel much faster. */
    public List<ExpenseDto> getLatest5ExpensesForCurrentUser() {
        ProfileEntity currentUser = profileService.getCurrentProfile();
        return expenseRepository.findTop5ByProfileIdOrderByDateDesc(currentUser.getId())
                .stream()
                .map(data -> modelMapper.map(data, ExpenseDto.class))
                .collect(Collectors.toList());
    }


    @Cacheable(cacheNames = CACHE_NAME, key = "#root.target.getCurrentUserId() + '_total'")
        /* COMMENT: Aggregate functions (SUM/COUNT) are expensive for the DB
           as the table grows. Caching the 'Total' is a high-impact optimization. */
    public BigDecimal getTotalExpenseForCurrentUser() {
        ProfileEntity currentUser = profileService.getCurrentProfile();
        BigDecimal total = expenseRepository.findTotalExpensePerProfileId(currentUser.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    // NOTE: DON'T cache complex filters with many parameters
    // because the number of possible cache keys (combinations of dates/keywords)
    // would explode and consume our Redis memory.
//    @Cacheable(cacheNames = CACHE_NAME, key = "#root.target.getCurrentUserId() + '_' + #startDate + '_' + #endDate + '_' + #keyword")
        /* COMMENT: We include all filter parameters in the key.
           This ensures that a search for 'Food' doesn't return the cached
           results for 'Rent'. */
    public List<ExpenseDto> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileEntity currentUser = profileService.getCurrentProfile();
        List<ExpenseEntity> filters = expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(currentUser.getId(), startDate, endDate, keyword, sort);
        return filters.stream()
                .map(data -> modelMapper.map(data, ExpenseDto.class))
                .collect(Collectors.toList());

    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#profileId + '_' + #date")
        /* COMMENT: Since the profileId is passed as an argument, we use it
           directly in the key to maintain data isolation. */
    public List<ExpenseDto> getExpensesForUsersOnDate(Long profileId, LocalDate date) {
        List<ExpenseEntity> lists = expenseRepository.findByProfileIdAndDate(profileId, date);

        return lists.stream()
                .map(entity -> {
                    // 1. Map basic fields (id, name, amount, date)
                    ExpenseDto dto = modelMapper.map(entity, ExpenseDto.class);

                    // 2. Explicitly map Category details to avoid Lazy loading issues/nulls
                    if (entity.getCategory() != null) {
                        dto.setCategoryId(entity.getCategory().getId());
                        dto.setCategoryName(entity.getCategory().getName());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Long getCurrentUserId() {
        return profileService.getCurrentProfile().getId();
    }
}