package com.example.moneymanager.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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

    public void sendEmail(String toEmail, String subject, String htmlContent) {
        try {
            log.info("Using API Key: {}", brevoApiKey);
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


//
//    private final JavaMailSender javaMailSender;
//
/// /    public EmailService(@Autowired(required = false) JavaMailSender javaMailSender) {
/// /        this.javaMailSender = javaMailSender;
/// /    }
//
//    @Value("${spring.mail.properties.mail.smtp.fromEmail}")
//    private String fromEmail;
//
//    public void sendEmail(String toEmail, String subject, String body) {
//        if (javaMailSender == null) {
//            log.error("JavaMailSender is null. Check your spring.mail configuration in application.yml");
//            throw new RuntimeException("Email configuration is missing or incorrect.");
//        }
//
//        try {
//            log.info("Attempting to send email to {}", toEmail);
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(fromEmail);
//            message.setTo(toEmail);
//            message.setSubject(subject);
//            message.setText(body);
//
//            javaMailSender.send(message);
//            log.info("Email sent successfully!");
//        } catch (Exception ex) {
//            log.error("SMTP Error: {}", ex.getMessage());
//            throw new RuntimeException("Email sending failed: " + ex.getMessage());
//        }
//    }
//}