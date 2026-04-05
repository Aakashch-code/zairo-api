package org.example.zairo.transaction.application.dto;


import jakarta.validation.constraints.*;
import lombok.*;
import org.example.zairo.transaction.domain.model.TransactionCategory;
import org.example.zairo.transaction.domain.model.TransactionCurrency;
import org.example.zairo.transaction.domain.model.TransactionSource;
import org.example.zairo.transaction.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {

    @NotNull
    private TransactionType type;

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull
    private TransactionCurrency currency;

    @NotNull
    private TransactionCategory category;

    @NotNull
    private LocalDate date;

    @NotNull
    private TransactionSource source;

    @Size(max = 500)
    private String note;
}