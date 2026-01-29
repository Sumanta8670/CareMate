package com.Sumanta.caremate.service;

import com.Sumanta.caremate.dto.AdminLoginRequest;
import com.Sumanta.caremate.dto.AuthResponse;
import com.Sumanta.caremate.dto.NurseDTO;
import com.Sumanta.caremate.dto.PageResponse;
import com.Sumanta.caremate.entity.AdminEntity;
import com.Sumanta.caremate.entity.NurseEntity;
import com.Sumanta.caremate.enums.UserRole;
import com.Sumanta.caremate.repository.AdminRepository;
import com.Sumanta.caremate.repository.NurseRepository;
import com.Sumanta.caremate.util.JWTUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final NurseRepository nurseRepository;

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

    public PageResponse<NurseDTO> getAllNurses(Pageable pageable) {
        Page<NurseEntity> nursePage = nurseRepository.findAll(pageable);

        List<NurseDTO> nurseDTOs = nursePage.getContent().stream()
                .map(this::convertToNurseDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                nurseDTOs,
                nursePage.getNumber(),
                nursePage.getSize(),
                nursePage.getTotalElements(),
                nursePage.getTotalPages(),
                nursePage.isLast()
        );
    }

    private NurseDTO convertToNurseDTO(NurseEntity nurse) {
        NurseDTO dto = new NurseDTO();
        dto.setId(nurse.getId());
        dto.setFullName(nurse.getFullName());
        dto.setMobileNo(nurse.getMobileNo());
        dto.setEmail(nurse.getEmail());
        dto.setProfileImage1(nurse.getProfileImage1());
        dto.setProfileImage2(nurse.getProfileImage2());
        dto.setEducationalQualification(nurse.getEducationalQualification());
        dto.setYearsOfExperience(nurse.getYearsOfExperience());
        dto.setAge(nurse.getAge());
        dto.setSpecializations(nurse.getSpecializations());
        dto.setStatus(nurse.getStatus());
        dto.setIsActive(nurse.getIsActive());
        dto.setCreatedAt(nurse.getCreatedAt());
        dto.setRating(0.0); // Will implement later with reviews
        dto.setTotalReviews(0);
        return dto;
    }

}