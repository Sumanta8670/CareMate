package com.Sumanta.caremate.service;

import com.Sumanta.caremate.dto.AdminLoginRequest;
import com.Sumanta.caremate.dto.AuthResponse;
import com.Sumanta.caremate.entity.AdminEntity;
import com.Sumanta.caremate.enums.UserRole;
import com.Sumanta.caremate.repository.AdminRepository;
import com.Sumanta.caremate.util.JWTUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    @PostConstruct
    public void initializeAdmins() {
        // Initialize predefined admins if they don't exist
        createAdminIfNotExists("janasumanta59@gmail.com", "Jana8670*");
        createAdminIfNotExists("tanushreeparamanik100@gmail.com", "Anutanu@123");
    }

    private void createAdminIfNotExists(String email, String password) {
        if (!adminRepository.existsByEmail(email)) {
            AdminEntity admin = new AdminEntity();
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(UserRole.ADMIN);
            admin.setIsActive(true);
            adminRepository.save(admin);
            log.info("Admin created: {}", email);
        }
    }

    public AuthResponse login(AdminLoginRequest request) {
        AdminEntity admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!admin.getIsActive()) {
            throw new RuntimeException("Admin account is deactivated");
        }

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(admin.getEmail());

        return new AuthResponse(
                token,
                UserRole.ADMIN,
                admin.getEmail(),
                "Admin login successful"
        );
    }
}