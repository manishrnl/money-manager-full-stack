package com.example.moneymanager.controller;


import com.example.moneymanager.dto.IncomeDto;
import com.example.moneymanager.services.IncomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeDto> addIncome(@RequestBody IncomeDto incomeDto) {
        log.info("Adding new income: {}", incomeDto.getName());
        IncomeDto saved = incomeService.addIncome(incomeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDto>> getIncomeForCurrentMonth() {
        return ResponseEntity.ok(incomeService.getIncomeForCurrentMonth());
    }

    @DeleteMapping("/{incomeId}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long incomeId) {
        incomeService.deleteIncome(incomeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/latest")
    public ResponseEntity<List<IncomeDto>> getLatestIncomes() {
        return ResponseEntity.ok(incomeService.getLatest5IncomesForCurrentUser());
    }
}