package com.example.moneymanager.controller;

import com.example.moneymanager.dto.ExpenseDto;
import com.example.moneymanager.dto.FilterDto;
import com.example.moneymanager.dto.IncomeDto;
import com.example.moneymanager.services.ExpenseService;
import com.example.moneymanager.services.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filters")

public class FilterController {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;


    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDto filterDto) {
        LocalDate startDate = filterDto.getStartDate() != null ? filterDto.getStartDate() : LocalDate.MIN;
        LocalDate endDate = filterDto.getEndDate() != null ? filterDto.getEndDate() : LocalDate.now();
        String keyword = filterDto.getKeyword() != null ? filterDto.getKeyword() : "";
        String sortField = filterDto.getSortField() != null ? filterDto.getSortField() : "date";
        Sort.Direction direction = "desc".equalsIgnoreCase(filterDto.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);

        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().body("Start date cannot be after end date.");
        }
        if ("income".equalsIgnoreCase(filterDto.getType())) {
            List<IncomeDto> incomeDto = incomeService.filterIncome(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(incomeDto);
        }
        if ("expense".equalsIgnoreCase(filterDto.getType())) {
            List<ExpenseDto> expenseDto = expenseService.filterExpenses(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(expenseDto);
        } else {
            return ResponseEntity.badRequest().body("Type is invalid . Check spelling for 'income' OR 'expense' ");
        }
    }
}
