package com.Sumanta.caremate.controller;

import com.Sumanta.caremate.dto.*;
import com.Sumanta.caremate.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin authentication and management")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/login")
    @Operation(summary = "Admin login", description = "Login endpoint for predefined admin users")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AdminLoginRequest request) {
        try {
            AuthResponse response = adminService.login(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if admin portal is accessible")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Admin portal is running", "OK"));
    }

    @GetMapping("/nurses")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get all nurses", description = "Get paginated list of all nurses")
    public ResponseEntity<ApiResponse<PageResponse<NurseDTO>>> getAllNurses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            PageResponse<NurseDTO> response = adminService.getAllNurses(pageable);
            return ResponseEntity.ok(new ApiResponse<>(true, "Nurses retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

}