package com.Sumanta.caremate.controller;

import com.Sumanta.caremate.dto.*;
import com.Sumanta.caremate.service.NurseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/nurse")
@RequiredArgsConstructor
@Tag(name = "Nurse/Caretaker", description = "Nurse and caretaker management")
@SecurityRequirement(name = "Bearer Authentication")
public class NurseController {

    private final NurseService nurseService;

    // ==================== AUTHENTICATION ====================

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Register as Nurse/Caretaker")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @ModelAttribute NurseRegistrationRequest request) {
        try {
            AuthResponse response = nurseService.register(request);
            return ResponseEntity.ok(new ApiResponse<>(true, response.getMessage(), response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Nurse/Caretaker login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody NurseLoginRequest request) {
        try {
            AuthResponse response = nurseService.login(request);
            return ResponseEntity.ok(new ApiResponse<>(true, response.getMessage(), response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ==================== PROFILE MANAGEMENT ====================

    @GetMapping("/profile")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Get own profile")
    public ResponseEntity<ApiResponse<NurseDTO>> getProfile(Authentication authentication) {
        try {
            String email = authentication.getName();
            NurseDTO nurse = nurseService.getProfileByEmail(email);
            return ResponseEntity.ok(new ApiResponse<>(true, "Profile retrieved successfully", nurse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Update profile")
    public ResponseEntity<ApiResponse<NurseDTO>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody NurseUpdateRequest request) {
        try {
            String email = authentication.getName();
            NurseDTO nurse = nurseService.updateProfile(email, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Profile updated successfully", nurse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/profile/status")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Update availability status")
    public ResponseEntity<ApiResponse<NurseDTO>> updateStatus(
            Authentication authentication,
            @Valid @RequestBody StatusUpdateRequest request) {
        try {
            String email = authentication.getName();
            NurseDTO nurse = nurseService.updateStatus(email, request.getStatus());
            return ResponseEntity.ok(new ApiResponse<>(true, "Status updated successfully", nurse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping(value = "/profile/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Update profile image")
    public ResponseEntity<ApiResponse<NurseDTO>> updateProfileImage(
            Authentication authentication,
            @RequestParam("image") MultipartFile image,
            @RequestParam("imageNumber") int imageNumber) {
        try {
            String email = authentication.getName();
            NurseDTO nurse = nurseService.updateProfileImage(email, image, imageNumber);
            return ResponseEntity.ok(new ApiResponse<>(true, "Profile image updated successfully", nurse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ==================== AVAILABILITY SCHEDULE ====================

    @PostMapping("/availability/schedule")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Create availability schedule")
    public ResponseEntity<ApiResponse<AvailabilityScheduleDTO>> createSchedule(
            Authentication authentication,
            @Valid @RequestBody CreateScheduleRequest request) {
        try {
            String email = authentication.getName();
            AvailabilityScheduleDTO schedule = nurseService.createSchedule(email, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Schedule created successfully", schedule));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/availability/schedule")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Get availability schedules")
    public ResponseEntity<ApiResponse<List<AvailabilityScheduleDTO>>> getSchedules(Authentication authentication) {
        try {
            String email = authentication.getName();
            List<AvailabilityScheduleDTO> schedules = nurseService.getSchedules(email);
            return ResponseEntity.ok(new ApiResponse<>(true, "Schedules retrieved successfully", schedules));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/availability/schedule/{id}")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Delete availability schedule")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(
            Authentication authentication,
            @PathVariable Long id) {
        try {
            String email = authentication.getName();
            nurseService.deleteSchedule(email, id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Schedule deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    // ==================== BOOKING MANAGEMENT ====================

    @GetMapping("/bookings")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Get all bookings")
    public ResponseEntity<ApiResponse<PageResponse<BookingDTO>>> getAllBookings(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String email = authentication.getName();
            Pageable pageable = PageRequest.of(page, size);
            PageResponse<BookingDTO> bookings = nurseService.getAllBookings(email, pageable);
            return ResponseEntity.ok(new ApiResponse<>(true, "Bookings retrieved successfully", bookings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/bookings/{id}")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Get booking details")
    public ResponseEntity<ApiResponse<BookingDTO>> getBookingById(
            Authentication authentication,
            @PathVariable Long id) {
        try {
            String email = authentication.getName();
            BookingDTO booking = nurseService.getBookingById(email, id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Booking retrieved successfully", booking));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/bookings/active")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Get active bookings")
    public ResponseEntity<ApiResponse<PageResponse<BookingDTO>>> getActiveBookings(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String email = authentication.getName();
            Pageable pageable = PageRequest.of(page, size);
            PageResponse<BookingDTO> bookings = nurseService.getActiveBookings(email, pageable);
            return ResponseEntity.ok(new ApiResponse<>(true, "Active bookings retrieved successfully", bookings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/bookings/history")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Get booking history")
    public ResponseEntity<ApiResponse<PageResponse<BookingDTO>>> getBookingHistory(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String email = authentication.getName();
            Pageable pageable = PageRequest.of(page, size);
            PageResponse<BookingDTO> bookings = nurseService.getBookingHistory(email, pageable);
            return ResponseEntity.ok(new ApiResponse<>(true, "Booking history retrieved successfully", bookings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PatchMapping("/bookings/{id}/accept")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Accept booking")
    public ResponseEntity<ApiResponse<BookingDTO>> acceptBooking(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody(required = false) BookingActionRequest request) {
        try {
            String email = authentication.getName();
            BookingActionRequest req = request != null ? request : new BookingActionRequest();
            BookingDTO booking = nurseService.acceptBooking(email, id, req);
            return ResponseEntity.ok(new ApiResponse<>(true, "Booking accepted successfully", booking));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PatchMapping("/bookings/{id}/reject")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Reject booking")
    public ResponseEntity<ApiResponse<BookingDTO>> rejectBooking(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody BookingActionRequest request) {
        try {
            String email = authentication.getName();
            BookingDTO booking = nurseService.rejectBooking(email, id, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Booking rejected successfully", booking));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PatchMapping("/bookings/{id}/complete")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Complete booking")
    public ResponseEntity<ApiResponse<BookingDTO>> completeBooking(
            Authentication authentication,
            @PathVariable Long id) {
        try {
            String email = authentication.getName();
            BookingDTO booking = nurseService.completeBooking(email, id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Booking completed successfully", booking));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/bookings/{id}/report")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Submit care report")
    public ResponseEntity<ApiResponse<BookingDTO>> submitCareReport(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody CareReportRequest request) {
        try {
            String email = authentication.getName();
            BookingDTO booking = nurseService.submitCareReport(email, id, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Care report submitted successfully", booking));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ==================== REVIEWS & RATINGS ====================

    @GetMapping("/reviews")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Get all reviews")
    public ResponseEntity<ApiResponse<PageResponse<ReviewDTO>>> getReviews(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String email = authentication.getName();
            Pageable pageable = PageRequest.of(page, size);
            PageResponse<ReviewDTO> reviews = nurseService.getReviews(email, pageable);
            return ResponseEntity.ok(new ApiResponse<>(true, "Reviews retrieved successfully", reviews));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/reviews/stats")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Get rating statistics")
    public ResponseEntity<ApiResponse<ReviewStatsDTO>> getReviewStats(Authentication authentication) {
        try {
            String email = authentication.getName();
            ReviewStatsDTO stats = nurseService.getReviewStats(email);
            return ResponseEntity.ok(new ApiResponse<>(true, "Review statistics retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/reviews/{id}/reply")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Reply to review")
    public ResponseEntity<ApiResponse<ReviewDTO>> replyToReview(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody ReviewReplyRequest request) {
        try {
            String email = authentication.getName();
            ReviewDTO review = nurseService.replyToReview(email, id, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Reply submitted successfully", review));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ==================== DASHBOARD & ANALYTICS ====================

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Get dashboard data")
    public ResponseEntity<ApiResponse<NurseDashboardDTO>> getDashboard(Authentication authentication) {
        try {
            String email = authentication.getName();
            NurseDashboardDTO dashboard = nurseService.getDashboard(email);
            return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard data retrieved successfully", dashboard));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/stats/bookings")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Get booking statistics")
    public ResponseEntity<ApiResponse<BookingStatsDTO>> getBookingStats(Authentication authentication) {
        try {
            String email = authentication.getName();
            BookingStatsDTO stats = nurseService.getBookingStats(email);
            return ResponseEntity.ok(new ApiResponse<>(true, "Booking statistics retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ==================== EARNINGS & PAYMENTS ====================

    @GetMapping("/earnings")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Get total earnings")
    public ResponseEntity<ApiResponse<EarningsDTO>> getTotalEarnings(Authentication authentication) {
        try {
            String email = authentication.getName();
            EarningsDTO earnings = nurseService.getTotalEarnings(email);
            return ResponseEntity.ok(new ApiResponse<>(true, "Earnings retrieved successfully", earnings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/earnings/monthly")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Get monthly earnings")
    public ResponseEntity<ApiResponse<List<MonthlyEarningsDTO>>> getMonthlyEarnings(Authentication authentication) {
        try {
            String email = authentication.getName();
            List<MonthlyEarningsDTO> earnings = nurseService.getMonthlyEarnings(email);
            return ResponseEntity.ok(new ApiResponse<>(true, "Monthly earnings retrieved successfully", earnings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/earnings/breakdown")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Get earnings breakdown")
    public ResponseEntity<ApiResponse<EarningsBreakdownDTO>> getEarningsBreakdown(Authentication authentication) {
        try {
            String email = authentication.getName();
            EarningsBreakdownDTO breakdown = nurseService.getEarningsBreakdown(email);
            return ResponseEntity.ok(new ApiResponse<>(true, "Earnings breakdown retrieved successfully", breakdown));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ==================== NOTIFICATIONS ====================

    @GetMapping("/notifications")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Get notifications")
    public ResponseEntity<ApiResponse<PageResponse<NotificationDTO>>> getNotifications(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            String email = authentication.getName();
            Pageable pageable = PageRequest.of(page, size);
            PageResponse<NotificationDTO> notifications = nurseService.getNotifications(email, pageable);
            return ResponseEntity.ok(new ApiResponse<>(true, "Notifications retrieved successfully", notifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PatchMapping("/notifications/{id}/read")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Mark notification as read")
    public ResponseEntity<ApiResponse<Void>> markNotificationAsRead(
            Authentication authentication,
            @PathVariable Long id) {
        try {
            String email = authentication.getName();
            nurseService.markNotificationAsRead(email, id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Notification marked as read"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage()));
        }
    }
}