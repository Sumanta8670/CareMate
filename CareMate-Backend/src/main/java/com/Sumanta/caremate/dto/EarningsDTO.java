package com.Sumanta.caremate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EarningsDTO {
    private BigDecimal totalEarnings;
    private BigDecimal monthlyEarnings;
    private BigDecimal weeklyEarnings;
    private BigDecimal dailyEarnings;
    private Integer totalCompletedBookings;
    private Integer monthlyCompletedBookings;
    private LocalDate lastPaymentDate;
    private BigDecimal averageBookingValue;
}