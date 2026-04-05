package org.example.zairo.authentication.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.zairo.authentication.application.dto.*;
import org.example.zairo.authentication.application.service.AuthService;
import org.example.zairo.authentication.domain.model.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

@Tag(name = "Authentication", description = "User authentication and workspace APIs")
public class AuthController {

    private final AuthService authService;

    // ===================== REGISTER =====================
    @Operation(summary = "Register user")
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // ===================== LOGIN =====================
    @Operation(summary = "Login user")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // ===================== GET WORKSPACE USERS =====================
    @Operation(summary = "Get workspace users")
    @GetMapping("/workspace/users")
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZER', 'ROLE_ADMIN')")
    public ResponseEntity<List<UserSummaryDTO>> getWorkspaceUsers(Authentication authentication) {

        return ResponseEntity.ok(
                authService.getTeamMembersInWorkspace(authentication)
        );
    }
    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZER', 'ROLE_ADMIN')")
    public ResponseEntity<String> updateUser(
            @PathVariable UUID userId,
            @RequestBody UpdateUserRequest request,
            Authentication authentication) {

        String response = authService.updateUser(userId, request, authentication);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZER', 'ROLE_ADMIN')")
    public ResponseEntity<String> deleteUser(
            @PathVariable UUID userId,
            Authentication authentication) {

        String response = authService.deleteUser(userId, authentication);
        return ResponseEntity.ok(response);
    }
}