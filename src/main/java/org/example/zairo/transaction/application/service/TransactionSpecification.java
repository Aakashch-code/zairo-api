package org.example.zairo.transaction.application.service;

import org.example.zairo.transaction.domain.model.Transaction;
import org.example.zairo.transaction.domain.model.TransactionCategory;
import org.example.zairo.transaction.domain.model.TransactionType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class TransactionSpecification {

    public static Specification<Transaction> filter(
            UUID workspaceId,
            TransactionType type,
            LocalDate startDate,
            LocalDate endDate,
            TransactionCategory category,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String keyword
    ) {
        return (root, query, cb) -> {

            var predicates = cb.conjunction();

            // 🔒 Workspace filter (Ensures data isolation)
            predicates = cb.and(predicates,
                    cb.equal(root.get("workspace").get("id"), workspaceId));

            if (type != null) {
                predicates = cb.and(predicates, cb.equal(root.get("type"), type));
            }

            if (startDate != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("date"), startDate));
            }

            if (endDate != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("date"), endDate));
            }

            if (category != null) {
                // Fixed: category is an Enum, not a relational entity with an ID
                predicates = cb.and(predicates, cb.equal(root.get("category"), category));
            }

            if (minAmount != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
            }

            if (maxAmount != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("amount"), maxAmount));
            }

            if (keyword != null && !keyword.isBlank()) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("note")), "%" + keyword.toLowerCase() + "%"));
            }

            return predicates;
        };
    }
}