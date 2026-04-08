package org.example.zairo.authentication.application.service;

import lombok.RequiredArgsConstructor;
import org.example.zairo.authentication.application.dto.*;
import org.example.zairo.authentication.application.exception.BadRequestException;
import org.example.zairo.authentication.application.exception.ForbiddenException;
import org.example.zairo.authentication.application.exception.NotFoundException;
import org.example.zairo.authentication.application.exception.UnauthorizedException;
import org.example.zairo.authentication.domain.model.FinanceWorkspace;
import org.example.zairo.authentication.domain.model.Users;
import org.example.zairo.authentication.domain.model.UsersRole;
import org.example.zairo.authentication.infrastructure.persistence.FinanceWorkspaceRepository;
import org.example.zairo.authentication.infrastructure.persistence.UserRepository;
import org.example.zairo.authentication.infrastructure.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final FinanceWorkspaceRepository workspaceRepository;

    @Transactional
    public String register(RegisterRequest request) {

        if (repo.existsByUsername(request.getUsername())
                || repo.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Credentials are already used");
        }

        FinanceWorkspace workspace;

        if (request.getRole() == UsersRole.ORGANIZER) {
            if (request.getWorkspaceName() == null || request.getWorkspaceName().isEmpty()) {
                throw new BadRequestException("Workspace name required");
            }

            workspace = new FinanceWorkspace();
            workspace.setName(request.getWorkspaceName());
            workspace.setInviteCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            workspaceRepository.save(workspace);

        } else {
            workspace = workspaceRepository.findByInviteCode(request.getInviteCode())
                    .orElseThrow(() -> new BadRequestException("Invalid invite code"));
        }

        Users user = new Users(
                null,
                workspace,
                request.getUsername(),
                request.getEmail(),
                encoder.encode(request.getPassword()),
                request.getRole()
        );

        repo.save(user);

        if (request.getRole() == UsersRole.ORGANIZER) {
            return "Organizer registered. Invite code: " + workspace.getInviteCode();
        }

        return "User registered successfully";
    }

    public AuthResponse login(LoginRequest request) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
                )
        );

        Users user = repo.findByUsernameOrEmail(request.getLogin())
                .orElseThrow(() -> new NotFoundException("User not found"));

        String token = jwtUtil.generateToken(user.getId(), user.getRole().toString());

        return new AuthResponse(token);
    }

    public List<UserSummaryDTO> getTeamMembersInWorkspace(Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());

        Users currentUser = repo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (currentUser.getWorkspace() == null) {
            throw new NotFoundException("User does not belong to a workspace.");
        }

        return repo.findUserSummariesByWorkspaceId(currentUser.getWorkspace().getId());
    }
    @Transactional
    public String deleteUser(UUID targetUserId, Authentication authentication) {
        UUID currentUserId = UUID.fromString(authentication.getName());
        Users currentUser = repo.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("Current user not found"));

        Users targetUser = repo.findById(targetUserId)
                .orElseThrow(() -> new NotFoundException("User to delete not found"));

        if (currentUser.getRole() == UsersRole.ORGANIZER) {
            if (!currentUser.getWorkspace().getId().equals(targetUser.getWorkspace().getId())) {
                throw new ForbiddenException("Forbidden: Cannot delete users outside your workspace");
            }
            if (currentUser.getId().equals(targetUser.getId())) {
                throw new ForbiddenException("Action not allowed: Cannot delete your own organizer account");
            }
        }

        repo.delete(targetUser);
        return "User deleted successfully";
    }
    @Transactional
    public String updateUser(UUID targetUserId, UpdateUserRequest request, Authentication authentication) {
        UUID currentUserId = UUID.fromString(authentication.getName());
        Users currentUser = repo.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("Current user not found"));

        Users targetUser = repo.findById(targetUserId)
                .orElseThrow(() -> new NotFoundException("User to update not found"));

        if (currentUser.getRole() == UsersRole.ORGANIZER) {
            if (!currentUser.getWorkspace().getId().equals(targetUser.getWorkspace().getId())) {
                throw new ForbiddenException("Forbidden: Cannot update users outside your workspace");
            }
        }

        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            targetUser.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            targetUser.setEmail(request.getEmail());
        }
        if (request.getRole() != null) {
            targetUser.setRole(request.getRole());
        }

        repo.save(targetUser);
        return "User updated successfully";
    }
}