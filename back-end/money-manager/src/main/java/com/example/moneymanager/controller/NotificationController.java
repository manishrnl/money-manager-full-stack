package com.example.moneymanager.controller;

import com.example.moneymanager.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
