package com.Sumanta.caremate.security;

import com.Sumanta.caremate.entity.AdminEntity;
import com.Sumanta.caremate.entity.NurseEntity;
import com.Sumanta.caremate.entity.PatientEntity;
import com.Sumanta.caremate.repository.AdminRepository;
import com.Sumanta.caremate.repository.NurseRepository;
import com.Sumanta.caremate.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final NurseRepository nurseRepository;
    private final PatientRepository patientRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Check Admin
        Optional<AdminEntity> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            return User.builder()
                    .username(admin.get().getEmail())
                    .password(admin.get().getPassword())
                    .authorities(Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + admin.get().getRole().name())))
                    .build();
        }

        // Check Nurse - UPDATED: Now uses actual password
        Optional<NurseEntity> nurse = nurseRepository.findByEmail(email);
        if (nurse.isPresent()) {
            return User.builder()
                    .username(nurse.get().getEmail())
                    .password(nurse.get().getPassword()) // UPDATED: Use encrypted password
                    .authorities(Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + nurse.get().getRole().name())))
                    .build();
        }

        // Check Patient - UPDATED: Now uses actual password
        Optional<PatientEntity> patient = patientRepository.findByEmail(email);
        if (patient.isPresent()) {
            return User.builder()
                    .username(patient.get().getEmail())
                    .password(patient.get().getPassword()) // UPDATED: Use encrypted password
                    .authorities(Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + patient.get().getRole().name())))
                    .build();
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}