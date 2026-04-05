package org.example.zairo.transaction.domain.model;

import lombok.Getter;

@Getter
public enum TransactionCategory {

    // INCOME
    SALARY(TransactionType.INCOME),
    BUSINESS(TransactionType.INCOME),
    FREELANCE(TransactionType.INCOME),
    INVESTMENT(TransactionType.INCOME),
    RENTAL(TransactionType.INCOME),
    INTEREST(TransactionType.INCOME),
    DIVIDEND(TransactionType.INCOME),
    BONUS(TransactionType.INCOME),
    GIFT(TransactionType.INCOME),

    // EXPENSE
    FOOD(TransactionType.EXPENSE),
    TRANSPORT(TransactionType.EXPENSE),
    HOUSING(TransactionType.EXPENSE),
    UTILITIES(TransactionType.EXPENSE),
    HEALTHCARE(TransactionType.EXPENSE),
    EDUCATION(TransactionType.EXPENSE),
    ENTERTAINMENT(TransactionType.EXPENSE),
    SHOPPING(TransactionType.EXPENSE),
    SUBSCRIPTION(TransactionType.EXPENSE),
    INSURANCE(TransactionType.EXPENSE),
    TAX(TransactionType.EXPENSE),
    EMI(TransactionType.EXPENSE),
    TRAVEL(TransactionType.EXPENSE),
    PERSONAL(TransactionType.EXPENSE),

    // UNIVERSAL
    OTHER(null);

    private final TransactionType type;

    TransactionCategory(TransactionType type) {
        this.type = type;
    }

}