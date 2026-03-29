package com.example.moneymanager.services;

import com.example.moneymanager.dto.ExpenseDto;
import com.example.moneymanager.dto.IncomeDto;
import com.example.moneymanager.dto.RecentTransactionDto;
import com.example.moneymanager.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor

public class DashboardService {
    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    public Map<String, Object> getDashboardData() {
        ProfileEntity currentUser = profileService.getCurrentProfile();

        BigDecimal totalIncome = incomeService.getTotalIncomeForCurrentUser();
        BigDecimal totalExpense = expenseService.getTotalExpenseForCurrentUser();
        BigDecimal totalBalance = totalIncome.subtract(totalExpense);

        List<IncomeDto> latestIncomes = incomeService.getLatest5IncomesForCurrentUser();
        List<ExpenseDto> latestExpenses = expenseService.getLatest5ExpensesForCurrentUser();

        List<RecentTransactionDto> recentTransactions = Stream.concat(
                        latestIncomes.stream().map(incomeDto -> mapIncomeToRecent(incomeDto, currentUser.getId())),
                        latestExpenses.stream().map(expenseDto -> mapExpenseToRecent(expenseDto, currentUser.getId()))
                ).sorted((a, b) -> {
                    int dateCmp = b.getDate().compareTo(a.getDate());
                    if (dateCmp != 0) return dateCmp;

                    if (a.getCreatedAt() != null && b.getCreatedAt() != null) {
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    }
                    return 0;
                })
                .limit(10)
                .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalBalance", totalBalance);
        response.put("totalIncome", totalIncome);
        response.put("totalExpenses", totalExpense);
        response.put("recent5Incomes", latestIncomes);
        response.put("recent5Expenses", latestExpenses);
        response.put("recentTransactions", recentTransactions);

        return response;
    }

    private RecentTransactionDto mapIncomeToRecent(IncomeDto income, Long profileId) {
        return RecentTransactionDto.builder()
                .id(income.getId())
                .profileId(profileId)
                .icon(income.getIcon())
                .name(income.getName())
                .amount(income.getAmount())
                .date(income.getDate())
                .type("income") // Explicitly set
                .build();
    }

    private RecentTransactionDto mapExpenseToRecent(ExpenseDto expense, Long profileId) {
        return RecentTransactionDto.builder()
                .id(expense.getId())
                .profileId(profileId)
                .icon(expense.getIcon())
                .name(expense.getName())
                .amount(expense.getAmount())
                .date(expense.getDate())
                .type("expense") // Explicitly set
                .build();
    }


}