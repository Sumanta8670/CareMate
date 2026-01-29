package com.Sumanta.caremate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CareReportRequest {

    @NotBlank(message = "Care report is required")
    @Size(max = 2000, message = "Care report cannot exceed 2000 characters")
    private String careReport;
}