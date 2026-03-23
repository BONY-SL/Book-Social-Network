package com.diphlk.book.controller;

import com.diphlk.book.dto.AuthenticateRequest;
import com.diphlk.book.dto.AuthenticationResponse;
import com.diphlk.book.dto.RegistrationRequest;
import com.diphlk.book.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication and authorization")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(
            @RequestBody @Valid RegistrationRequest registrationRequest
    ) throws MessagingException {
        authenticationService.register(registrationRequest);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticateRequest authenticateRequest
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticateRequest));
    }

    @GetMapping("/activate-account")
    public void activateAccount(@RequestParam("token") String token) throws MessagingException {
        authenticationService.activateAccount(token);
    }

}
