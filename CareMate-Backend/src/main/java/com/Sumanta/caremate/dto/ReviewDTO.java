package com.Sumanta.caremate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private Long bookingId;
    private Long patientId;
    private String patientName;
    private Long nurseId;
    private String nurseName;
    private Integer rating;
    private String comment;
    private String nurseReply;
    private LocalDateTime repliedAt;
    private LocalDateTime createdAt;
}