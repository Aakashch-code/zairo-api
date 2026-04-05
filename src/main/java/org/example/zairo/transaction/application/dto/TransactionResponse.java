package org.example.zairo.transaction.application.dto;

import lombok.*;
import org.example.zairo.transaction.domain.model.TransactionCategory;
import org.example.zairo.transaction.domain.model.TransactionCurrency;
import org.example.zairo.transaction.domain.model.TransactionSource;
import org.example.zairo.transaction.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long id;
    private UUID userId;

    private TransactionType type;
    private BigDecimal amount;
    private TransactionCurrency currency;
    private TransactionCategory category;
    private TransactionSource source;
    private LocalDate date;
    private String note;


}