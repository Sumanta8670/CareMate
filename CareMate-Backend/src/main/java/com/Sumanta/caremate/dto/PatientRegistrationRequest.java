package com.Sumanta.caremate.dto;

import com.Sumanta.caremate.enums.PatientCategory;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientRegistrationRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobileNo;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Hospital report image is required")
    private MultipartFile hospitalReportImage;

    @NotNull(message = "Age is required")
    @Min(value = 50, message = "Age must be at least 50")
    private Integer age;

    @NotNull(message = "Patient category is required")
    private PatientCategory category;

    @NotBlank(message = "Family mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Family mobile number must be 10 digits")
    private String familyMobileNo;

    @NotBlank(message = "Family email is required")
    @Email(message = "Invalid family email format")
    private String familyEmail;
}