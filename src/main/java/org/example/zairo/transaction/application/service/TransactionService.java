package org.example.zairo.transaction.application.service;

import lombok.RequiredArgsConstructor;
import org.example.zairo.authentication.application.service.SecuredService;
import org.example.zairo.authentication.domain.model.FinanceWorkspace;
import org.example.zairo.authentication.domain.model.Users;
import org.example.zairo.authentication.infrastructure.persistence.UserRepository;
import org.example.zairo.transaction.application.dto.SavingResponse;
import org.example.zairo.transaction.application.dto.TransactionFilterRequest;
import org.example.zairo.transaction.application.dto.TransactionRequest;
import org.example.zairo.transaction.application.dto.TransactionResponse;
import org.example.zairo.transaction.domain.exception.ResourceNotFoundException;
import org.example.zairo.transaction.domain.model.Transaction;
import org.example.zairo.transaction.domain.model.TransactionType;
import org.example.zairo.transaction.domain.validator.TransactionValidator;
import org.example.zairo.transaction.infrastrucutre.mapper.TransactionMapper;
import org.example.zairo.transaction.infrastrucutre.persistence.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService extends SecuredService {

    private final TransactionMapper mapper;
    private final TransactionValidator validator;
    private final TransactionRepository repository;
    private final UserRepository userRepository;

    public TransactionResponse create(TransactionRequest request) {
        FinanceWorkspace currentWorkspace = getCurrentUserWorkspace();
        Transaction transaction = mapper.toEntity(request, currentUserId());
        transaction.setWorkspace(currentWorkspace);
        validator.validate(transaction);
        return mapper.toResponse(repository.save(transaction));
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> findAll(String keyword, Pageable pageable) {
        UUID workspaceId = getCurrentUserWorkspace().getId();

        Page<Transaction> transactions = (keyword != null && !keyword.isBlank())
                ? repository.searchByWorkspaceIdAndKeyword(workspaceId, keyword, pageable)
                : repository.findByWorkspaceId(workspaceId, pageable);

        return transactions.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public TransactionResponse findById(long id) {
        return mapper.toResponse(getTransactionOrThrow(id));
    }

    public TransactionResponse update(long id, TransactionRequest request) {
        Transaction existing = getTransactionOrThrow(id);
        mapper.updateEntityFromDto(request, existing);
        validator.validate(existing);
        return mapper.toResponse(repository.save(existing));
    }

    public void delete(long id) {
        repository.delete(getTransactionOrThrow(id));
    }

    @Transactional(readOnly = true)
    public SavingResponse calculateNetPosition() {
        UUID workspaceId = getCurrentUserWorkspace().getId();

        BigDecimal income = getSum(workspaceId, TransactionType.INCOME);
        BigDecimal expense = getSum(workspaceId, TransactionType.EXPENSE);

        return new SavingResponse(income, expense, income.subtract(expense));
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> filterTransactions(TransactionFilterRequest filter, Pageable pageable) {
        UUID workspaceId = getCurrentUserWorkspace().getId();

        Page<Transaction> transactions = repository.findAll(
                TransactionSpecification.filter(
                        workspaceId,
                        filter.getType(),
                        filter.getStartDate(),
                        filter.getEndDate(),
                        filter.getCategory(),
                        filter.getMinAmount(),
                        filter.getMaxAmount(),
                        filter.getKeyword()
                ),
                pageable
        );

        return transactions.map(mapper::toResponse);
    }

    private Transaction getTransactionOrThrow(long id) {
        UUID workspaceId = getCurrentUserWorkspace().getId();
        return repository.findByIdAndWorkspaceId(id, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found or access denied"));
    }

    private BigDecimal getSum(UUID workspaceId, TransactionType type) {
        return Optional.ofNullable(repository.sumAmountByWorkspaceIdAndType(workspaceId, type))
                .orElse(BigDecimal.ZERO);
    }

    private FinanceWorkspace getCurrentUserWorkspace() {
        UUID userId = currentUserId(); // Use the existing SecuredService method
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getWorkspace() == null) {
            throw new IllegalStateException("User does not belong to a workspace");
        }
        return user.getWorkspace();
    }
}