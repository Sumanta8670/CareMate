package com.Sumanta.caremate.dto;

import com.Sumanta.caremate.enums.PatientCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NurseUpdateRequest {

    private String educationalQualification;

    @Min(value = 0, message = "Years of experience cannot be negative")
    private Integer yearsOfExperience;

    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 70, message = "Age must not exceed 70")
    private Integer age;

    private Set<PatientCategory> specializations;
}
