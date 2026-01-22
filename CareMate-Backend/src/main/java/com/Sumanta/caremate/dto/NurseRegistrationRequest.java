package com.Sumanta.caremate.dto;

import com.Sumanta.caremate.enums.PatientCategory;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NurseRegistrationRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobileNo;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Profile image 1 is required")
    private MultipartFile profileImage1;

    @NotNull(message = "Profile image 2 is required")
    private MultipartFile profileImage2;

    @NotBlank(message = "Educational qualification is required")
    private String educationalQualification;

    @NotNull(message = "Years of experience is required")
    @Min(value = 0, message = "Years of experience cannot be negative")
    private Integer yearsOfExperience;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 70, message = "Age must not exceed 70")
    private Integer age;

    @NotEmpty(message = "At least one specialization is required")
    private Set<PatientCategory> specializations;
}