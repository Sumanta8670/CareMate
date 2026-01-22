package com.Sumanta.caremate.controller;

import com.Sumanta.caremate.dto.*;
import com.Sumanta.caremate.service.NurseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nurse")
@RequiredArgsConstructor
@Tag(name = "Nurse/Caretaker", description = "Nurse and caretaker registration and authentication")
public class NurseController {

    private final NurseService nurseService;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Register as Nurse/Caretaker",
            description = "Register a new nurse or caretaker with required documents and details")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @ModelAttribute NurseRegistrationRequest request) {
        try {
            AuthResponse response = nurseService.register(request);
            return ResponseEntity.ok(new ApiResponse<>(true, response.getMessage(), response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Nurse/Caretaker login",
            description = "Login with name, mobile number and email")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody NurseLoginRequest request) {
        try {
            AuthResponse response = nurseService.login(request);
            return ResponseEntity.ok(new ApiResponse<>(true, response.getMessage(), response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}