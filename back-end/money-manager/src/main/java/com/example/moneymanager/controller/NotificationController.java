package com.example.moneymanager.controller;

import com.example.moneymanager.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/daily")
    public void sendDailyNotification() {
        notificationService.sendDailyExpenseReminder();
    }

    @PostMapping("/expense")
    public void sendExpenseNotification() {
        notificationService.sendDailyExpenseSummary();
    }

    @PostMapping("/incomes/{email}")
    public ResponseEntity<?> sendIncomeReport(@PathVariable String email, @RequestBody List<Map<String, Object>> incomeBody) {
        notificationService.sendIncomesNotificationFromFrontend(email, incomeBody);
        return ResponseEntity.ok("Email sent successfully using live data");
    }

    @PostMapping("/expenses/{email}")
    public ResponseEntity<?> sendExpensesReport(@PathVariable String email,
                                         @RequestBody List<Map<String, Object>> expenseBody) {
        notificationService.sendExpensesNotificationFromFrontend(email, expenseBody);
        return ResponseEntity.ok("Email sent successfully using live data");
    }


    @PostMapping("/filterData/{email}")
    public ResponseEntity<?> sendFilteredNotificationFromFrontend(@PathVariable String email,
                                                @RequestBody List<Map<String, Object>> filteredBody) {
        notificationService.sendFilteredNotificationFromFrontend(email, filteredBody);
        return ResponseEntity.ok("Email sent successfully using live data");
    }

}
