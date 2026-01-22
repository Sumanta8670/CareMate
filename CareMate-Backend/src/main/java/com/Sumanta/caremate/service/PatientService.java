package com.Sumanta.caremate.service;

import com.Sumanta.caremate.dto.AuthResponse;
import com.Sumanta.caremate.dto.PatientLoginRequest;
import com.Sumanta.caremate.dto.PatientRegistrationRequest;
import com.Sumanta.caremate.entity.PatientEntity;
import com.Sumanta.caremate.enums.UserRole;
import com.Sumanta.caremate.repository.PatientRepository;
import com.Sumanta.caremate.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;
    private final FileStorageService fileStorageService;
    private final EmailService emailService;
    private final JWTUtil jwtUtil;

    @Transactional
    public AuthResponse register(PatientRegistrationRequest request) {
        // Check if email or mobile already exists
        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        if (patientRepository.existsByMobileNo(request.getMobileNo())) {
            throw new RuntimeException("Mobile number already registered");
        }

        // Store hospital report image
        String reportImagePath = fileStorageService.storeFile(
                request.getHospitalReportImage(), "patients/reports");

        // Create patient entity
        PatientEntity patient = new PatientEntity();
        patient.setFullName(request.getFullName());
        patient.setMobileNo(request.getMobileNo());
        patient.setEmail(request.getEmail());
        patient.setHospitalReportImage(reportImagePath);
        patient.setAge(request.getAge());
        patient.setCategory(request.getCategory());
        patient.setFamilyMobileNo(request.getFamilyMobileNo());
        patient.setFamilyEmail(request.getFamilyEmail());
        patient.setRole(UserRole.PATIENT);
        patient.setIsActive(true);

        PatientEntity savedPatient = patientRepository.save(patient);

        // Send welcome email to patient
        emailService.sendPatientRegistrationEmail(
                savedPatient.getEmail(),
                savedPatient.getFullName()
        );

        // Send notification email to family member
        emailService.sendFamilyNotificationEmail(
                savedPatient.getFamilyEmail(),
                savedPatient.getFullName()
        );

        // Generate JWT token
        String token = jwtUtil.generateToken(savedPatient.getEmail());

        log.info("Patient registered successfully: {}", savedPatient.getEmail());

        return new AuthResponse(
                token,
                UserRole.PATIENT,
                savedPatient.getEmail(),
                "Patient registration successful. Welcome emails sent!"
        );
    }

    public AuthResponse login(PatientLoginRequest request) {
        PatientEntity patient = patientRepository
                .findByFullNameAndMobileNoAndEmail(
                        request.getFullName(),
                        request.getMobileNo(),
                        request.getEmail()
                )
                .orElseThrow(() -> new RuntimeException("Invalid credentials. Please check your details."));

        if (!patient.getIsActive()) {
            throw new RuntimeException("Your account has been deactivated. Please contact admin.");
        }

        String token = jwtUtil.generateToken(patient.getEmail());

        log.info("Patient logged in successfully: {}", patient.getEmail());

        return new AuthResponse(
                token,
                UserRole.PATIENT,
                patient.getEmail(),
                "Login successful"
        );
    }
}