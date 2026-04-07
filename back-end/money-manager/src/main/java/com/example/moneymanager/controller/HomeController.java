package com.example.moneymanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequestMapping({"/status", "/health"})
@RequiredArgsConstructor
public class HomeController {

    private final RestTemplate restTemplate;

    @Value("${money.manager.backend.url}")
    String backendURL;

    private final String selfURL = backendURL + "/api/v1/status";

    @GetMapping
    public String healthCheck() {
        return "Application is running";
    }


//    @Scheduled(fixedRate = 300000)
//    public void keepAlive() {
//        try {
//
//            String response = restTemplate.getForObject(selfURL, String.class);
//            log.info("Self-ping successful: {}", response);
//        } catch (Exception ex) {
//            log.error("Self-ping failed. {}", ex.getMessage());
//        }
//    }
}
