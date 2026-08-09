package org.example.zairo.transaction.infrastructure.persistence;

import org.example.zairo.transaction.domain.model.Transaction;
import org.example.zairo.transaction.domain.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    Page<Transaction> findByWorkspaceId(UUID workspaceId, Pageable pageable);

    Optional<Transaction> findByIdAndWorkspaceId(Long id, UUID workspaceId);

    @Query("SELECT t FROM Transaction t WHERE t.workspace.id = :workspaceId AND LOWER(t.category) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Transaction> searchByWorkspaceIdAndKeyword(@Param("workspaceId") UUID wosrkspaceId, @Param("keyword") String keyword, Pageable pageable);

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.workspace.id = :workspaceId AND t.type = :type
    """)
    BigDecimal sumAmountByWorkspaceIdAndType(
            @Param("workspaceId") UUID workspaceId,
            @Param("type") TransactionType type
    );
}