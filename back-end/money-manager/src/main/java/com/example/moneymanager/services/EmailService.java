package com.example.moneymanager.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {


    @Value("${brevo_api_key}") // Add your Brevo API Key to Render Env
    private String brevoApiKey;

    @Value("${brevo_from_email}")
    private String fromEmail;

    private final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";
    private final RestTemplate restTemplate = new RestTemplate();
    /**
     * COMMENT: We use @Async instead of caching.
     * Caching an email would stop it from being sent multiple times.
     * @Async moves the API call to a background thread so the user doesn't
     * have to wait for the Brevo API to respond.
     */
    @Async
    public void sendEmail(String toEmail, String subject, String htmlContent) {
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            // Construct the JSON payload for Brevo API
            Map<String, Object> body = Map.of(
                    "sender", Map.of("email", fromEmail, "name", "Money Manager"),
                    "to", List.of(Map.of("email", toEmail)),
                    "subject", subject,
                    "htmlContent", htmlContent
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_API_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email sent successfully via API to: {}", toEmail);
            } else {
                log.error("Failed to send email. Brevo responded with: {}", response.getBody());
            }
        } catch (Exception e) {
            log.error("Error sending email via API: {}", e.getMessage());
        }
    }
}
