package com.Sumanta.caremate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyEarningsDTO {
    private String month; // e.g., "January 2026"
    private Integer year;
    private Integer monthNumber; // 1-12
    private BigDecimal earnings;
    private Integer bookingsCompleted;
}