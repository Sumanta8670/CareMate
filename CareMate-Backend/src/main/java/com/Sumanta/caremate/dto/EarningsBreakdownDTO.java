package com.Sumanta.caremate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EarningsBreakdownDTO {
    private BigDecimal totalEarnings;
    private List<MonthlyEarningsDTO> monthlyBreakdown;
    private BigDecimal highestMonthEarnings;
    private String highestEarningMonth;
}