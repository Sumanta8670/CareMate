package com.Sumanta.caremate.controller;

import com.Sumanta.caremate.dto.*;
import com.Sumanta.caremate.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
@Tag(name = "Patient", description = "Patient registration and authentication")
public class PatientController {

    private final PatientService patientService;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Register as Patient",
            description = "Register a new patient with required medical documents and details")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @ModelAttribute PatientRegistrationRequest request) {
        try {
            AuthResponse response = patientService.register(request);
            return ResponseEntity.ok(new ApiResponse<>(true, response.getMessage(), response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Patient login",
            description = "Login with name, mobile number and email")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody PatientLoginRequest request) {
        try {
            AuthResponse response = patientService.login(request);
            return ResponseEntity.ok(new ApiResponse<>(true, response.getMessage(), response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}