package com.example.moneymanager.services;

import com.example.moneymanager.dto.IncomeDto;
import com.example.moneymanager.entity.CategoryEntity;
import com.example.moneymanager.entity.IncomeEntity;
import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.exceptions.ResourceNotFoundException;
import com.example.moneymanager.repository.CategoryRepository;
import com.example.moneymanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class IncomeService {
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public IncomeDto addIncome(IncomeDto incomeDto) {
        ProfileEntity currentUser = profileService.getCurrentProfile();

        // Corrected: Use getCategoryId() for the lookup error message
        CategoryEntity category = categoryRepository.findById(incomeDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + incomeDto.getCategoryId()));

        IncomeEntity incomeEntity = modelMapper.map(incomeDto, IncomeEntity.class);

        // Set relations and ensure ID is fresh
        incomeEntity.setId(null);
        incomeEntity.setProfile(currentUser);
        incomeEntity.setCategory(category);

        incomeEntity = incomeRepository.save(incomeEntity);
        return modelMapper.map(incomeEntity, IncomeDto.class);
    }

    public List<IncomeDto> getIncomeForCurrentMonth() {
        ProfileEntity currentUser = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        return incomeRepository.findByProfileIdAndDateBetween(currentUser.getId(), startDate, endDate)
                .stream()
                .map(data -> modelMapper.map(data, IncomeDto.class))
                .collect(Collectors.toList());
    }

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

    public List<IncomeDto> getLatest5IncomesForCurrentUser() {
        ProfileEntity currentUser = profileService.getCurrentProfile();
        // Assuming you have this method in your IncomeRepository
        return incomeRepository.findTop5ByProfileIdOrderByDateDesc(currentUser.getId())
                .stream()
                .map(data -> modelMapper.map(data, IncomeDto.class))
                .collect(Collectors.toList());
    }

    public BigDecimal getTotalIncomeForCurrentUser() {
        ProfileEntity currentUser = profileService.getCurrentProfile();
        BigDecimal total = incomeRepository.findTotalIncomePerProfileId(currentUser.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<IncomeDto> filterIncome(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileEntity currentUser = profileService.getCurrentProfile();

        List<IncomeEntity> filters =
                incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(currentUser.getId(), startDate, endDate, keyword, sort);
        return filters.stream()
                .map(data -> modelMapper.map(data, IncomeDto.class))
                .collect(Collectors.toList());

    }

}