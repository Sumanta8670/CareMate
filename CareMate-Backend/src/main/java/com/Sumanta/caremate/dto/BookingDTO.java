package com.Sumanta.caremate.dto;

import com.Sumanta.caremate.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Long id;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private String patientMobile;
    private Long nurseId;
    private String nurseName;
    private String nurseEmail;
    private String nurseMobile;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalAmount;
    private BigDecimal baseCharge;
    private BigDecimal subscriptionCharge;
    private BookingStatus status;
    private String patientNotes;
    private String nurseNotes;
    private String careReport;
    private LocalDateTime acceptedAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime completedAt;
    private String rejectionReason;
    private LocalDateTime createdAt;
}