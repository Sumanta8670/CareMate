package com.Sumanta.caremate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatsDTO {
    private Long totalBookings;
    private Long pendingBookings;
    private Long acceptedBookings;
    private Long rejectedBookings;
    private Long completedBookings;
    private Long cancelledBookings;
    private Long activeBookings;
    private Double acceptanceRate;
    private Double completionRate;
}