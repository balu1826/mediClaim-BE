package com.mediclaim.mediclaim.controller;

import com.mediclaim.mediclaim.dto.user.CreatePatientRequest;
import com.mediclaim.mediclaim.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/patients")
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public ResponseEntity<Void> createPatient(
            @Valid @RequestBody CreatePatientRequest request) {

        userService.createPatient(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }
}