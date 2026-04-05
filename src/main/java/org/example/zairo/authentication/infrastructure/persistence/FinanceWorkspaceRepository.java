package org.example.zairo.authentication.infrastructure.persistence;

import org.example.zairo.authentication.domain.model.FinanceWorkspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FinanceWorkspaceRepository extends JpaRepository<FinanceWorkspace, UUID> {

    Optional<FinanceWorkspace> findByInviteCode(String inviteCode);

}