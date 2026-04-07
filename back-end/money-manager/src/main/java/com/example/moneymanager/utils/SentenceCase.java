package com.example.moneymanager.utils;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SentenceCase {
    //    @Bean
    public String convertToSentenceCase(String sentence) {
        if (sentence == null || sentence.isEmpty()) {
            return "";
        }

        // 1. Split the sentence into words by whitespace
        String[] words = sentence.trim().split("\\s+");
        StringBuilder convertedText = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                // 2. Capitalize first letter, lowercase the rest
                String formattedWord = word.substring(0, 1).toUpperCase() +
                        word.substring(1).toLowerCase();

                // 3. Append to the builder with a space
                convertedText.append(formattedWord).append(" ");
            }
        }

        // 4. Return trimmed string to remove the trailing space
        return convertedText.toString().trim();
    }
}