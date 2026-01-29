package com.Sumanta.caremate.enums;

public enum BookingStatus {
    PENDING,        // Waiting for nurse acceptance
    ACCEPTED,       // Nurse accepted the booking
    REJECTED,       // Nurse rejected the booking
    IN_PROGRESS,    // Care is currently being provided
    COMPLETED,      // Care completed successfully
    CANCELLED       // Cancelled by patient or admin
}