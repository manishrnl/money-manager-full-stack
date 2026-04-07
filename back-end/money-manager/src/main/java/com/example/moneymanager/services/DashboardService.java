package com.example.moneymanager.services;

import com.example.moneymanager.dto.ExpenseDto;
import com.example.moneymanager.dto.IncomeDto;
import com.example.moneymanager.dto.RecentTransactionDto;
import com.example.moneymanager.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
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

    public static final String DASHBOARD_CACHE = "dashboard";

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    @Cacheable(cacheNames = DASHBOARD_CACHE, key = "#root.target.getCurrentUserId()")
    public Map<String, Object> getDashboardData() {
        log.info("Dashboard cache MISS for user ID: {}", getCurrentUserId());

        ProfileEntity currentUser = profileService.getCurrentProfile();

        BigDecimal totalIncome = incomeService.getTotalIncomeForCurrentUser();
        BigDecimal totalExpense = expenseService.getTotalExpenseForCurrentUser();

        totalIncome = totalIncome != null ? totalIncome : BigDecimal.ZERO;
        totalExpense = totalExpense != null ? totalExpense : BigDecimal.ZERO;

        BigDecimal totalBalance = totalIncome.subtract(totalExpense);

        List<IncomeDto> latestIncomes = incomeService.getLatest5IncomesForCurrentUser();
        List<ExpenseDto> latestExpenses = expenseService.getLatest5ExpensesForCurrentUser();

        List<RecentTransactionDto> recentTransactions = Stream.concat(
                        latestIncomes.stream().map(i -> mapIncomeToRecent(i, currentUser.getId())),
                        latestExpenses.stream().map(e -> mapExpenseToRecent(e, currentUser.getId()))
                )
                .sorted(this::compareRecentTransactions)
                .limit(10)
                .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalBalance", totalBalance);
        response.put("totalIncome", totalIncome);
        response.put("totalExpenses", totalExpense);
        response.put("recentTransactions", recentTransactions);

        return response;
    }

    private int compareRecentTransactions(RecentTransactionDto a, RecentTransactionDto b) {
        if (a.getDate() == null && b.getDate() == null) return 0;
        if (a.getDate() == null) return 1;
        if (b.getDate() == null) return -1;

        int dateCmp = b.getDate().compareTo(a.getDate());
        if (dateCmp != 0) return dateCmp;

        if (a.getCreatedAt() != null && b.getCreatedAt() != null) {
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        }
        return 0;
    }

    private RecentTransactionDto mapIncomeToRecent(IncomeDto income, Long profileId) {
        return RecentTransactionDto.builder()
                .id(income.getId())
                .profileId(profileId)
                .icon(income.getIcon())
                .name(income.getName())
                .amount(income.getAmount())
                .date(income.getDate())
                .type("income")
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
                .type("expense")
                .build();
    }

    public Long getCurrentUserId() {
        return profileService.getCurrentProfile().getId();
    }
}

//import com.example.moneymanager.dto.ExpenseDto;
//import com.example.moneymanager.dto.IncomeDto;
//import com.example.moneymanager.dto.RecentTransactionDto;
//import com.example.moneymanager.entity.ProfileEntity;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//
//public class DashboardService {
//    private final IncomeService incomeService;
//    private final ExpenseService expenseService;
//    private final ProfileService profileService;
//
//
//    private static final String DASHBOARD_CACHE = "dashboard";
//
//    @Cacheable(cacheNames = DASHBOARD_CACHE, key = "#root.target.getCurrentUserId()")
//
//        /* COMMENT: @Cacheable is used because the dashboard is the most visited page.
//           We use the User ID as the key so each user sees only their own data.
//           If the data is in Redis, this entire function body is skipped.
//        */
//
//    public Map<String, Object> getDashboardData() {
//        ProfileEntity currentUser = profileService.getCurrentProfile();
//
//        BigDecimal totalIncome = incomeService.getTotalIncomeForCurrentUser();
//        BigDecimal totalExpense = expenseService.getTotalExpenseForCurrentUser();
//
//        // Handle potential nulls from DB sums
//        totalIncome = (totalIncome != null) ? totalIncome : BigDecimal.ZERO;
//        totalExpense = (totalExpense != null) ? totalExpense : BigDecimal.ZERO;
//        BigDecimal totalBalance = totalIncome.subtract(totalExpense);
//
//        List<IncomeDto> latestIncomes = incomeService.getLatest5IncomesForCurrentUser();
//        List<ExpenseDto> latestExpenses = expenseService.getLatest5ExpensesForCurrentUser();
//
//        List<RecentTransactionDto> recentTransactions = Stream.concat(
//                        latestIncomes.stream().map(incomeDto -> mapIncomeToRecent(incomeDto, currentUser.getId())),
//                        latestExpenses.stream().map(expenseDto -> mapExpenseToRecent(expenseDto, currentUser.getId()))
//                ).sorted((a, b) -> {
//                    // 1. Handle Null Dates Safely
//                    if (a.getDate() == null && b.getDate() == null) return 0;
//                    if (a.getDate() == null) return 1;  // Move nulls to the end
//                    if (b.getDate() == null) return -1;
//
//                    int dateCmp = b.getDate().compareTo(a.getDate());
//                    if (dateCmp != 0) return dateCmp;
//
//                    // 2. Secondary sort by CreatedAt if dates are identical
//                    if (a.getCreatedAt() != null && b.getCreatedAt() != null) {
//                        return b.getCreatedAt().compareTo(a.getCreatedAt());
//                    }
//                    return 0;
//                })
//                .limit(10)
//                .collect(Collectors.toList());
//
//        Map<String, Object> response = new LinkedHashMap<>();
//        response.put("totalBalance", totalBalance);
//        response.put("totalIncome", totalIncome);
//        response.put("totalExpenses", totalExpense);
//        response.put("recentTransactions", recentTransactions);
//
//        return response;
//    }
//    private RecentTransactionDto mapIncomeToRecent(IncomeDto income, Long profileId) {
//        return RecentTransactionDto.builder()
//                .id(income.getId())
//                .profileId(profileId)
//                .icon(income.getIcon())
//                .name(income.getName())
//                .amount(income.getAmount())
//                .date(income.getDate())
//                .type("income") // Explicitly set
//                .build();
//    }
//
//    private RecentTransactionDto mapExpenseToRecent(ExpenseDto expense, Long profileId) {
//        return RecentTransactionDto.builder()
//                .id(expense.getId())
//                .profileId(profileId)
//                .icon(expense.getIcon())
//                .name(expense.getName())
//                .amount(expense.getAmount())
//                .date(expense.getDate())
//                .type("expense") // Explicitly set
//                .build();
//    }
//
//    public Long getCurrentUserId() {
//        /* COMMENT: SpEL needs this method to exist on the 'target' (this class)
//           to generate the unique key for Redis.
//        */
//        return profileService.getCurrentProfile().getId();
//    }
//}