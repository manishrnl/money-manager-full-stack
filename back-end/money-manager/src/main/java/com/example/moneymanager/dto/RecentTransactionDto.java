package com.example.moneymanager.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class RecentTransactionDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long profileId;
    private String icon;
    private String name;
    private BigDecimal amount;
    private LocalDate date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String type;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            this.date = LocalDate.now();
        } else {
            this.date = date;
        }
    }
}
