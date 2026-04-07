package com.example.moneymanager.services;

import com.example.moneymanager.dto.IncomeDto;
import com.example.moneymanager.entity.CategoryEntity;
import com.example.moneymanager.entity.IncomeEntity;
import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.exceptions.ResourceNotFoundException;
import com.example.moneymanager.repository.CategoryRepository;
import com.example.moneymanager.repository.IncomeRepository;
import com.example.moneymanager.utils.SentenceCase;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class IncomeService {
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final SentenceCase sentenceCase;


    private static final String CACHE_NAME = "incomes";

    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_NAME, allEntries = true),           // Clears ALL income caches for this user
            @CacheEvict(cacheNames = DashboardService.DASHBOARD_CACHE, key = "#root.target.getCurrentUserId()")
    })
    public IncomeDto addIncome(IncomeDto incomeDto) {
        ProfileEntity currentUser = profileService.getCurrentProfile();

        // Corrected: Use getCategoryId() for the lookup error message
        CategoryEntity category = categoryRepository.findById(incomeDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + incomeDto.getCategoryId()));

        IncomeEntity incomeEntity = modelMapper.map(incomeDto, IncomeEntity.class);

        // Set relations and ensure ID is fresh
        incomeEntity.setId(null);
        incomeEntity.setName(sentenceCase.convertToSentenceCase(incomeDto.getName()));
        incomeEntity.setProfile(currentUser);
        incomeEntity.setCategory(category);

        incomeEntity = incomeRepository.save(incomeEntity);
        return modelMapper.map(incomeEntity, IncomeDto.class);
    }


    @Cacheable(cacheNames = CACHE_NAME, key = "'month_' + #root.target.getCurrentUserId()")
        /* COMMENT: Monthly data is perfect for caching as it's viewed frequently.
           We prefix the key with 'month_' to distinguish it from other lists. */
    public List<IncomeDto> getIncomeForCurrentMonth() {
        ProfileEntity currentUser = profileService.getCurrentProfile();
        ZoneId istZone = ZoneId.of("Asia/Kolkata");
        LocalDate now = LocalDate.now(istZone);

        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        // CRITICAL DEBUG LOGS
        System.out.println("--- DEBUG: Fetching Incomes ---");
        System.out.println("User ID: " + currentUser.getId());
        System.out.println("Range: " + startDate + " to " + endDate);

        List<IncomeEntity> results = incomeRepository.findByProfileIdAndDateBetween(currentUser.getId(), startDate, endDate);
        System.out.println("Raw Records Found: " + results.size());

        return results.stream()
                .map(data -> modelMapper.map(data, IncomeDto.class))
                .collect(Collectors.toList());
    }


    @Cacheable(cacheNames = CACHE_NAME, key = "'latest_' + #root.target.getCurrentUserId()")
        /* COMMENT: This is often used on dashboards. Caching this prevents
           the 'LIMIT 5' SQL query from hitting the DB on every page refresh. */
    public List<IncomeDto> getLatest5IncomesForCurrentUser() {
        ProfileEntity currentUser = profileService.getCurrentProfile();
        // Assuming you have this method in your IncomeRepository
        return incomeRepository.findTop5ByProfileIdOrderByDateDesc(currentUser.getId())
                .stream()
                .map(data -> modelMapper.map(data, IncomeDto.class))
                .collect(Collectors.toList());
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_NAME, allEntries = true),
            @CacheEvict(cacheNames = DashboardService.DASHBOARD_CACHE, key = "#root.target.getCurrentUserId()")
    })
    public void deleteIncome(Long incomeId) {
        ProfileEntity currentUser = profileService.getCurrentProfile();

        IncomeEntity entity = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new ResourceNotFoundException("Income entry not found with ID: " + incomeId));

        // Security Check: Verify ownership
        if (!entity.getProfile().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized to delete this income entry");
        }

        incomeRepository.delete(entity);
    }


    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_NAME, allEntries = true),
            @CacheEvict(cacheNames = DashboardService.DASHBOARD_CACHE, key = "#root.target.getCurrentUserId()")
    })
    public void updateIncomeById(Long incomeId, IncomeDto incomeDto) {
        ProfileEntity currentUser = profileService.getCurrentProfile();
        IncomeEntity entity = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new ResourceNotFoundException("Income entry not found with ID: " + incomeId));
        if (!entity.getProfile().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized to delete this income entry");
        }
        entity.setIcon(incomeDto.getIcon());
        entity.setAmount(incomeDto.getAmount());
        entity.setName(incomeDto.getName());
        entity.setDate(incomeDto.getDate());
        entity.setDate(incomeDto.getDate());
        CategoryEntity category = categoryRepository.findById(incomeDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + incomeDto.getCategoryId()));
        entity.setCategory(category);

        incomeRepository.save(entity);
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "'total_' + #root.target.getCurrentUserId()")
        /* COMMENT: Aggregation queries (SUM) are expensive for the DB.
           Caching the total is a huge performance win for the UI. */
    public BigDecimal getTotalIncomeForCurrentUser() {
        ProfileEntity currentUser = profileService.getCurrentProfile();
        BigDecimal total = incomeRepository.findTotalIncomePerProfileId(currentUser.getId());
        return total != null ? total : BigDecimal.ZERO;
    }


    // NOTE: DON'T cache complex filters with many parameters
    // because the number of possible cache keys (combinations of dates/keywords)
    // would explode and consume our Redis memory.
    //  @Cacheable(cacheNames = CACHE_NAME, key = "#root.target.getCurrentUserId() + '_' + #startDate + '_' + #endDate + '_' + #keyword")
    public List<IncomeDto> filterIncome(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileEntity currentUser = profileService.getCurrentProfile();

        List<IncomeEntity> filters =
                incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(currentUser.getId(), startDate, endDate, keyword, sort);
        return filters.stream()
                .map(data -> modelMapper.map(data, IncomeDto.class))
                .collect(Collectors.toList());

    }

    public Long getCurrentUserId() {
        return profileService.getCurrentProfile().getId();
    }
}