package com.Sumanta.caremate.dto;

import com.Sumanta.caremate.enums.NurseStatus;
import com.Sumanta.caremate.enums.PatientCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NurseDTO {
    private Long id;
    private String fullName;
    private String mobileNo;
    private String email;
    private String profileImage1;
    private String profileImage2;
    private String educationalQualification;
    private Integer yearsOfExperience;
    private Integer age;
    private Set<PatientCategory> specializations;
    private NurseStatus status;
    private Boolean isActive;
    private Double rating;
    private Integer totalReviews;
    private LocalDateTime createdAt;
}

