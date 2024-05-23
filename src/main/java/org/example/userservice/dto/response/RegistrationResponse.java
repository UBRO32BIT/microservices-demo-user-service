package org.example.userservice.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {
    private UserResponse data;
    private String token;
}
