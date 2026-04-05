package org.example.zairo.transaction.domain.validator;

import org.example.zairo.transaction.domain.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionValidator {

    public void validate(Transaction transaction) {

        if (transaction.getCategory().getType() != null &&
                transaction.getCategory().getType() != transaction.getType()) {

            throw new IllegalArgumentException("Invalid category for type");
        }
    }
}