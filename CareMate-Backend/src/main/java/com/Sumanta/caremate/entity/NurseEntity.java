package com.Sumanta.caremate.entity;

import com.Sumanta.caremate.enums.NurseStatus;
import com.Sumanta.caremate.enums.PatientCategory;
import com.Sumanta.caremate.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "nurses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NurseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String mobileNo;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // ADDED: Password field

    @Column(nullable = false)
    private String profileImage1;

    @Column(nullable = false)
    private String profileImage2;

    @Column(nullable = false)
    private String educationalQualification;

    @Column(nullable = false)
    private Integer yearsOfExperience;

    @Column(nullable = false)
    private Integer age;

    @ElementCollection(targetClass = PatientCategory.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "nurse_specializations", joinColumns = @JoinColumn(name = "nurse_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Set<PatientCategory> specializations = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NurseStatus status = NurseStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.NURSE;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}