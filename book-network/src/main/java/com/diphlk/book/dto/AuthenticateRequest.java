package com.diphlk.book.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticateRequest {
    @NotEmpty(message = "Email must not be empty")
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotEmpty(message = "Password must not be empty")
    @NotBlank(message = "Password must not be blank")
    private String password;
}
