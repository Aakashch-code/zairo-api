package org.example.zairo.authentication.application.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public abstract class SecuredService {

    public UUID currentUserId() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            throw new AccessDeniedException("Unauthenticated");
        }

        try {
            return (UUID) auth.getPrincipal();
        } catch (ClassCastException e) {
            throw new AccessDeniedException("Invalid authentication principal");
        }
    }
}