package com.Sumanta.caremate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NurseDashboardDTO {
    private Long totalBookings;
    private Long activeBookings;
    private Long completedBookings;
    private Long pendingBookings;
    private BigDecimal totalEarnings;
    private BigDecimal monthlyEarnings;
    private Double averageRating;
    private Integer totalReviews;
    private Long unreadNotifications;
}