package com.Sumanta.caremate.service;

import com.Sumanta.caremate.dto.AuthResponse;
import com.Sumanta.caremate.dto.NurseLoginRequest;
import com.Sumanta.caremate.dto.NurseRegistrationRequest;
import com.Sumanta.caremate.entity.NurseEntity;
import com.Sumanta.caremate.enums.NurseStatus;
import com.Sumanta.caremate.enums.UserRole;
import com.Sumanta.caremate.repository.NurseRepository;
import com.Sumanta.caremate.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NurseService {

    private final NurseRepository nurseRepository;
    private final FileStorageService fileStorageService;
    private final EmailService emailService;
    private final JWTUtil jwtUtil;

    @Transactional
    public AuthResponse register(NurseRegistrationRequest request) {
        // Check if email or mobile already exists
        if (nurseRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        if (nurseRepository.existsByMobileNo(request.getMobileNo())) {
            throw new RuntimeException("Mobile number already registered");
        }

        // Store profile images
        String profileImage1Path = fileStorageService.storeFile(
                request.getProfileImage1(), "nurses/profiles");
        String profileImage2Path = fileStorageService.storeFile(
                request.getProfileImage2(), "nurses/profiles");

        // Create nurse entity
        NurseEntity nurse = new NurseEntity();
        nurse.setFullName(request.getFullName());
        nurse.setMobileNo(request.getMobileNo());
        nurse.setEmail(request.getEmail());
        nurse.setProfileImage1(profileImage1Path);
        nurse.setProfileImage2(profileImage2Path);
        nurse.setEducationalQualification(request.getEducationalQualification());
        nurse.setYearsOfExperience(request.getYearsOfExperience());
        nurse.setAge(request.getAge());
        nurse.setSpecializations(request.getSpecializations());
        nurse.setStatus(NurseStatus.AVAILABLE);
        nurse.setRole(UserRole.NURSE);
        nurse.setIsActive(true);

        NurseEntity savedNurse = nurseRepository.save(nurse);

        // Send welcome email
        emailService.sendNurseRegistrationEmail(savedNurse.getEmail(), savedNurse.getFullName());

        // Generate JWT token
        String token = jwtUtil.generateToken(savedNurse.getEmail());

        log.info("Nurse registered successfully: {}", savedNurse.getEmail());

        return new AuthResponse(
                token,
                UserRole.NURSE,
                savedNurse.getEmail(),
                "Nurse registration successful. Welcome email sent!"
        );
    }

    public AuthResponse login(NurseLoginRequest request) {
        NurseEntity nurse = nurseRepository
                .findByFullNameAndMobileNoAndEmail(
                        request.getFullName(),
                        request.getMobileNo(),
                        request.getEmail()
                )
                .orElseThrow(() -> new RuntimeException("Invalid credentials. Please check your details."));

        if (!nurse.getIsActive()) {
            throw new RuntimeException("Your account has been deactivated. Please contact admin.");
        }

        String token = jwtUtil.generateToken(nurse.getEmail());

        log.info("Nurse logged in successfully: {}", nurse.getEmail());

        return new AuthResponse(
                token,
                UserRole.NURSE,
                nurse.getEmail(),
                "Login successful"
        );
    }
}