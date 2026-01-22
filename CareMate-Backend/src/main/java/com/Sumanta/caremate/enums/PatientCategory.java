package com.Sumanta.caremate.enums;

public enum PatientCategory {
    FULL_BED_REST("Full Bed Rest Patient", 3000),
    PARTIAL_BED_REST("Partial Bed Rest Patient", 2500),
    WHEELCHAIR_DEPENDENT("Wheelchair Dependent Patient", 2800),
    MENTAL_PATIENT("Mental Patient", 3500),
    CRITICAL_PATIENT("Critical Patient", 4500);

    private final String displayName;
    private final double baseCharge;

    PatientCategory(String displayName, double baseCharge) {
        this.displayName = displayName;
        this.baseCharge = baseCharge;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getBaseCharge() {
        return baseCharge;
    }
}