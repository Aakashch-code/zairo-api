package org.example.zairo.authentication.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.zairo.authentication.domain.model.UsersRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {
    private String username;
    private String email;
    private UsersRole role;

}