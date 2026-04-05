package org.example.zairo.transaction.application.dto;

import java.math.BigDecimal;

public record SavingResponse(
        BigDecimal income,
        BigDecimal expense,
        BigDecimal net
) {
}