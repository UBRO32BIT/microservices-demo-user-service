package org.example.userservice.dto.response;

import lombok.*;
import org.example.userservice.model.UserRole;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private UserRole role;
    private String profilePicture;
}
