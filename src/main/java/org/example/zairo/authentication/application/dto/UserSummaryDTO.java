package org.example.zairo.authentication.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.zairo.authentication.domain.model.UsersRole;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {
    private UUID userId;
    private String username;
    private String email;
    private UsersRole role;
}