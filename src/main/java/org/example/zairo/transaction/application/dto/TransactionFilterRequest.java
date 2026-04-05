package org.example.zairo.transaction.application.dto;

import lombok.Data;
import org.example.zairo.transaction.domain.model.TransactionCategory;
import org.example.zairo.transaction.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionFilterRequest {
    private TransactionType type;
    private LocalDate startDate;
    private LocalDate endDate;
    private TransactionCategory category;

    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String keyword;
}