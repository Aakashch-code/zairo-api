package org.example.zairo.authentication.application.dto;

import lombok.Data;
import org.example.zairo.authentication.domain.model.UsersRole;

@Data
public class UpdateUserRequest {
    private String username;
    private String email;
    private UsersRole role;
}