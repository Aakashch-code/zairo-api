package org.example.zairo.ai.application.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.zairo.transaction.application.dto.SavingResponse;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Data
public class FinancialData {

    private String workspaceName;
    private String organizerName;
    private SavingResponse transactionSummary;
    private String aiInsight;
}
