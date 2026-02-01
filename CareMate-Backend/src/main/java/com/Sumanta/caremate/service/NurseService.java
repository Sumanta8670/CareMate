package com.Sumanta.caremate.service;

import com.Sumanta.caremate.dto.*;
import com.Sumanta.caremate.entity.*;
import com.Sumanta.caremate.enums.*;
import com.Sumanta.caremate.repository.*;
import com.Sumanta.caremate.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NurseService {

    private final NurseRepository nurseRepository;
    private final AvailabilityScheduleRepository scheduleRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final NotificationRepository notificationRepository;
    private final FileStorageService fileStorageService;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // ==================== REGISTRATION & LOGIN ====================

    @Transactional
    public AuthResponse register(NurseRegistrationRequest request) {
        if (nurseRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        if (nurseRepository.existsByMobileNo(request.getMobileNo())) {
            throw new RuntimeException("Mobile number already registered");
        }

        String profileImage1Path = fileStorageService.storeFile(request.getProfileImage1(), "nurses/profiles");
        String profileImage2Path = fileStorageService.storeFile(request.getProfileImage2(), "nurses/profiles");

        NurseEntity nurse = new NurseEntity();
        nurse.setFullName(request.getFullName());
        nurse.setMobileNo(request.getMobileNo());
        nurse.setEmail(request.getEmail());
        nurse.setPassword(passwordEncoder.encode(request.getPassword()));
        nurse.setProfileImage1(profileImage1Path);
        nurse.setProfileImage2(profileImage2Path);
        nurse.setEducationalQualification(request.getEducationalQualification());
        nurse.setYearsOfExperience(request.getYearsOfExperience());
        nurse.setAge(request.getAge());
        nurse.setSpecializations(request.getSpecializations());
        nurse.setStatus(NurseStatus.AVAILABLE);
        nurse.setRole(UserRole.NURSE);
        nurse.setIsActive(true);

        NurseEntity savedNurse = nurseRepository.save(nurse);
        emailService.sendNurseRegistrationEmail(savedNurse.getEmail(), savedNurse.getFullName());
        String token = jwtUtil.generateToken(savedNurse.getEmail());

        log.info("Nurse registered successfully: {}", savedNurse.getEmail());
        return new AuthResponse(token, UserRole.NURSE, savedNurse.getEmail(), "Nurse registration successful. Welcome email sent!");
    }

    public AuthResponse login(NurseLoginRequest request) {
        NurseEntity nurse = nurseRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!nurse.getIsActive()) {
            throw new RuntimeException("Your account has been deactivated. Please contact admin.");
        }

        if (!passwordEncoder.matches(request.getPassword(), nurse.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(nurse.getEmail());
        log.info("Nurse logged in successfully: {}", nurse.getEmail());
        return new AuthResponse(token, UserRole.NURSE, nurse.getEmail(), "Login successful");
    }

    // ==================== PROFILE MANAGEMENT ====================

    public NurseDTO getProfileByEmail(String email) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));
        return convertToDTO(nurse);
    }

    @Transactional
    public NurseDTO updateProfile(String email, NurseUpdateRequest request) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        if (request.getEducationalQualification() != null) {
            nurse.setEducationalQualification(request.getEducationalQualification());
        }
        if (request.getYearsOfExperience() != null) {
            nurse.setYearsOfExperience(request.getYearsOfExperience());
        }
        if (request.getAge() != null) {
            nurse.setAge(request.getAge());
        }
        if (request.getSpecializations() != null && !request.getSpecializations().isEmpty()) {
            nurse.setSpecializations(request.getSpecializations());
        }

        nurseRepository.save(nurse);
        log.info("Nurse profile updated: {}", email);
        return convertToDTO(nurse);
    }

    @Transactional
    public NurseDTO updateStatus(String email, NurseStatus status) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        nurse.setStatus(status);
        nurseRepository.save(nurse);

        log.info("Nurse status updated to {}: {}", status, email);
        return convertToDTO(nurse);
    }

    @Transactional
    public NurseDTO updateProfileImage(String email, MultipartFile image, int imageNumber) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        if (imageNumber != 1 && imageNumber != 2) {
            throw new RuntimeException("Invalid image number. Use 1 or 2.");
        }

        String newImagePath = fileStorageService.storeFile(image, "nurses/profiles");

        if (imageNumber == 1) {
            fileStorageService.deleteFile(nurse.getProfileImage1());
            nurse.setProfileImage1(newImagePath);
        } else {
            fileStorageService.deleteFile(nurse.getProfileImage2());
            nurse.setProfileImage2(newImagePath);
        }

        nurseRepository.save(nurse);
        log.info("Nurse profile image {} updated: {}", imageNumber, email);
        return convertToDTO(nurse);
    }

    // ==================== AVAILABILITY SCHEDULE ====================

    @Transactional
    public AvailabilityScheduleDTO createSchedule(String email, CreateScheduleRequest request) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        if (scheduleRepository.existsByNurseAndDayOfWeekAndIsActive(nurse, request.getDayOfWeek(), true)) {
            throw new RuntimeException("Schedule already exists for " + request.getDayOfWeek());
        }

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new RuntimeException("Start time must be before end time");
        }

        AvailabilityScheduleEntity schedule = new AvailabilityScheduleEntity();
        schedule.setNurse(nurse);
        schedule.setDayOfWeek(request.getDayOfWeek());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setIsActive(true);

        AvailabilityScheduleEntity saved = scheduleRepository.save(schedule);
        log.info("Schedule created for nurse: {} on {}", email, request.getDayOfWeek());
        return convertToScheduleDTO(saved);
    }

    public List<AvailabilityScheduleDTO> getSchedules(String email) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        return scheduleRepository.findByNurse(nurse).stream()
                .map(this::convertToScheduleDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSchedule(String email, Long scheduleId) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        AvailabilityScheduleEntity schedule = scheduleRepository.findByIdAndNurse(scheduleId, nurse)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        scheduleRepository.delete(schedule);
        log.info("Schedule deleted for nurse: {}", email);
    }

    // ==================== BOOKING MANAGEMENT ====================

    public PageResponse<BookingDTO> getAllBookings(String email, Pageable pageable) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        Page<BookingEntity> bookingPage = bookingRepository.findByNurse(nurse, pageable);
        return convertToBookingPageResponse(bookingPage);
    }

    public BookingDTO getBookingById(String email, Long bookingId) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        BookingEntity booking = bookingRepository.findByIdAndNurse(bookingId, nurse)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        return convertToBookingDTO(booking);
    }

    public PageResponse<BookingDTO> getActiveBookings(String email, Pageable pageable) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        Page<BookingEntity> bookingPage = bookingRepository.findByNurseAndStatus(nurse, BookingStatus.IN_PROGRESS, pageable);
        return convertToBookingPageResponse(bookingPage);
    }

    public PageResponse<BookingDTO> getBookingHistory(String email, Pageable pageable) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        Page<BookingEntity> bookingPage = bookingRepository.findByNurseAndStatus(nurse, BookingStatus.COMPLETED, pageable);
        return convertToBookingPageResponse(bookingPage);
    }

    @Transactional
    public BookingDTO acceptBooking(String email, Long bookingId, BookingActionRequest request) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        BookingEntity booking = bookingRepository.findByIdAndNurse(bookingId, nurse)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Only pending bookings can be accepted");
        }

        booking.setStatus(BookingStatus.ACCEPTED);
        booking.setAcceptedAt(LocalDateTime.now());
        if (request.getNotes() != null) {
            booking.setNurseNotes(request.getNotes());
        }

        nurse.setStatus(NurseStatus.ON_DUTY);
        nurseRepository.save(nurse);

        BookingEntity saved = bookingRepository.save(booking);

        notificationService.createNotification(
                booking.getPatient().getId(),
                UserRole.PATIENT,
                NotificationType.BOOKING_ACCEPTED,
                "Booking Accepted",
                "Your booking has been accepted by " + nurse.getFullName(),
                bookingId
        );

        log.info("Booking {} accepted by nurse: {}", bookingId, email);
        return convertToBookingDTO(saved);
    }

    @Transactional
    public BookingDTO rejectBooking(String email, Long bookingId, BookingActionRequest request) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        BookingEntity booking = bookingRepository.findByIdAndNurse(bookingId, nurse)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Only pending bookings can be rejected");
        }

        booking.setStatus(BookingStatus.REJECTED);
        booking.setRejectedAt(LocalDateTime.now());
        booking.setRejectionReason(request.getReason());

        BookingEntity saved = bookingRepository.save(booking);

        notificationService.createNotification(
                booking.getPatient().getId(),
                UserRole.PATIENT,
                NotificationType.BOOKING_REJECTED,
                "Booking Rejected",
                "Your booking has been rejected by " + nurse.getFullName(),
                bookingId
        );

        log.info("Booking {} rejected by nurse: {}", bookingId, email);
        return convertToBookingDTO(saved);
    }

    @Transactional
    public BookingDTO completeBooking(String email, Long bookingId) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        BookingEntity booking = bookingRepository.findByIdAndNurse(bookingId, nurse)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.IN_PROGRESS && booking.getStatus() != BookingStatus.ACCEPTED) {
            throw new RuntimeException("Only active bookings can be completed");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        booking.setCompletedAt(LocalDateTime.now());

        nurse.setStatus(NurseStatus.AVAILABLE);
        nurseRepository.save(nurse);

        BookingEntity saved = bookingRepository.save(booking);

        notificationService.createNotification(
                booking.getPatient().getId(),
                UserRole.PATIENT,
                NotificationType.BOOKING_COMPLETED,
                "Booking Completed",
                "Your booking with " + nurse.getFullName() + " has been completed",
                bookingId
        );

        log.info("Booking {} completed by nurse: {}", bookingId, email);
        return convertToBookingDTO(saved);
    }

    @Transactional
    public BookingDTO submitCareReport(String email, Long bookingId, CareReportRequest request) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        BookingEntity booking = bookingRepository.findByIdAndNurse(bookingId, nurse)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setCareReport(request.getCareReport());
        BookingEntity saved = bookingRepository.save(booking);

        log.info("Care report submitted for booking {} by nurse: {}", bookingId, email);
        return convertToBookingDTO(saved);
    }

    // ==================== REVIEWS & RATINGS ====================

    public PageResponse<ReviewDTO> getReviews(String email, Pageable pageable) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        Page<ReviewEntity> reviewPage = reviewRepository.findByNurse(nurse, pageable);

        List<ReviewDTO> reviews = reviewPage.getContent().stream()
                .map(this::convertToReviewDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                reviews,
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.isLast()
        );
    }

    public ReviewStatsDTO getReviewStats(String email) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        Double avgRating = reviewRepository.calculateAverageRating(nurse);
        Long totalReviews = reviewRepository.countByNurse(nurse);

        ReviewStatsDTO stats = new ReviewStatsDTO();
        stats.setAverageRating(avgRating != null ? avgRating : 0.0);
        stats.setTotalReviews(totalReviews.intValue());
        stats.setFiveStarCount(reviewRepository.countByNurseAndRating(nurse, 5).intValue());
        stats.setFourStarCount(reviewRepository.countByNurseAndRating(nurse, 4).intValue());
        stats.setThreeStarCount(reviewRepository.countByNurseAndRating(nurse, 3).intValue());
        stats.setTwoStarCount(reviewRepository.countByNurseAndRating(nurse, 2).intValue());
        stats.setOneStarCount(reviewRepository.countByNurseAndRating(nurse, 1).intValue());

        return stats;
    }

    @Transactional
    public ReviewDTO replyToReview(String email, Long reviewId, ReviewReplyRequest request) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        ReviewEntity review = reviewRepository.findByIdAndNurse(reviewId, nurse)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setNurseReply(request.getReply());
        review.setRepliedAt(LocalDateTime.now());

        ReviewEntity saved = reviewRepository.save(review);
        log.info("Nurse replied to review {}: {}", reviewId, email);
        return convertToReviewDTO(saved);
    }

    // ==================== DASHBOARD & ANALYTICS ====================

    public NurseDashboardDTO getDashboard(String email) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        NurseDashboardDTO dashboard = new NurseDashboardDTO();
        dashboard.setTotalBookings(bookingRepository.countByNurse(nurse));
        dashboard.setActiveBookings(bookingRepository.countByNurseAndStatus(nurse, BookingStatus.IN_PROGRESS));
        dashboard.setCompletedBookings(bookingRepository.countByNurseAndStatus(nurse, BookingStatus.COMPLETED));
        dashboard.setPendingBookings(bookingRepository.countByNurseAndStatus(nurse, BookingStatus.PENDING));
        dashboard.setTotalEarnings(bookingRepository.calculateTotalEarnings(nurse));

        LocalDateTime firstDayOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        dashboard.setMonthlyEarnings(bookingRepository.calculateEarningsSince(nurse, firstDayOfMonth));

        Double avgRating = reviewRepository.calculateAverageRating(nurse);
        dashboard.setAverageRating(avgRating != null ? avgRating : 0.0);
        dashboard.setTotalReviews(reviewRepository.countByNurse(nurse).intValue());
        dashboard.setUnreadNotifications(notificationRepository.countByUserIdAndUserRoleAndIsRead(nurse.getId(), UserRole.NURSE, false));

        return dashboard;
    }

    // ==================== EARNINGS & PAYMENTS ====================

    public EarningsDTO getTotalEarnings(String email) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime startOfWeek = now.minusDays(7);
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        EarningsDTO earnings = new EarningsDTO();
        earnings.setTotalEarnings(bookingRepository.calculateTotalEarnings(nurse));
        earnings.setMonthlyEarnings(bookingRepository.calculateEarningsSince(nurse, startOfMonth));
        earnings.setWeeklyEarnings(bookingRepository.calculateEarningsSince(nurse, startOfWeek));
        earnings.setDailyEarnings(bookingRepository.calculateEarningsSince(nurse, startOfDay));

        Long totalCompleted = bookingRepository.countByNurseAndStatus(nurse, BookingStatus.COMPLETED);
        earnings.setTotalCompletedBookings(totalCompleted.intValue());

        Long monthlyCompleted = bookingRepository.countByNurseAndStatusAndCompletedAtAfter(
                nurse, BookingStatus.COMPLETED, startOfMonth);
        earnings.setMonthlyCompletedBookings(monthlyCompleted.intValue());

        if (totalCompleted > 0) {
            BigDecimal avgValue = earnings.getTotalEarnings().divide(
                    BigDecimal.valueOf(totalCompleted), 2, java.math.RoundingMode.HALF_UP);
            earnings.setAverageBookingValue(avgValue);
        } else {
            earnings.setAverageBookingValue(BigDecimal.ZERO);
        }

        return earnings;
    }

    public List<MonthlyEarningsDTO> getMonthlyEarnings(String email) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        List<MonthlyEarningsDTO> monthlyEarnings = new java.util.ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for (int i = 11; i >= 0; i--) {
            LocalDate monthDate = currentDate.minusMonths(i);
            LocalDateTime startOfMonth = monthDate.withDayOfMonth(1).atStartOfDay();
            LocalDateTime endOfMonth = monthDate.withDayOfMonth(
                    monthDate.lengthOfMonth()).atTime(23, 59, 59);

            BigDecimal earnings = bookingRepository.calculateEarningsInRange(
                    nurse, startOfMonth, endOfMonth);
            Long bookingsCount = bookingRepository.countByNurseAndStatusAndCompletedAtBetween(
                    nurse, BookingStatus.COMPLETED, startOfMonth, endOfMonth);

            MonthlyEarningsDTO dto = new MonthlyEarningsDTO();
            dto.setMonth(monthDate.getMonth().toString() + " " + monthDate.getYear());
            dto.setYear(monthDate.getYear());
            dto.setMonthNumber(monthDate.getMonthValue());
            dto.setEarnings(earnings != null ? earnings : BigDecimal.ZERO);
            dto.setBookingsCompleted(bookingsCount.intValue());

            monthlyEarnings.add(dto);
        }

        return monthlyEarnings;
    }

    public EarningsBreakdownDTO getEarningsBreakdown(String email) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        List<MonthlyEarningsDTO> monthlyBreakdown = getMonthlyEarnings(email);

        BigDecimal totalEarnings = bookingRepository.calculateTotalEarnings(nurse);

        MonthlyEarningsDTO highest = monthlyBreakdown.stream()
                .max((a, b) -> a.getEarnings().compareTo(b.getEarnings()))
                .orElse(new MonthlyEarningsDTO());

        EarningsBreakdownDTO breakdown = new EarningsBreakdownDTO();
        breakdown.setTotalEarnings(totalEarnings);
        breakdown.setMonthlyBreakdown(monthlyBreakdown);
        breakdown.setHighestMonthEarnings(highest.getEarnings());
        breakdown.setHighestEarningMonth(highest.getMonth());

        return breakdown;
    }

    // ==================== BOOKING STATISTICS ====================

    public BookingStatsDTO getBookingStats(String email) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        BookingStatsDTO stats = new BookingStatsDTO();
        stats.setTotalBookings(bookingRepository.countByNurse(nurse));
        stats.setPendingBookings(bookingRepository.countByNurseAndStatus(nurse, BookingStatus.PENDING));
        stats.setAcceptedBookings(bookingRepository.countByNurseAndStatus(nurse, BookingStatus.ACCEPTED));
        stats.setRejectedBookings(bookingRepository.countByNurseAndStatus(nurse, BookingStatus.REJECTED));
        stats.setCompletedBookings(bookingRepository.countByNurseAndStatus(nurse, BookingStatus.COMPLETED));
        stats.setCancelledBookings(bookingRepository.countByNurseAndStatus(nurse, BookingStatus.CANCELLED));
        stats.setActiveBookings(bookingRepository.countByNurseAndStatus(nurse, BookingStatus.IN_PROGRESS));

        // Calculate acceptance rate
        Long totalRequests = stats.getAcceptedBookings() + stats.getRejectedBookings();
        if (totalRequests > 0) {
            stats.setAcceptanceRate((stats.getAcceptedBookings().doubleValue() / totalRequests) * 100);
        } else {
            stats.setAcceptanceRate(0.0);
        }

        // Calculate completion rate
        Long totalAccepted = stats.getAcceptedBookings() + stats.getCompletedBookings();
        if (totalAccepted > 0) {
            stats.setCompletionRate((stats.getCompletedBookings().doubleValue() / totalAccepted) * 100);
        } else {
            stats.setCompletionRate(0.0);
        }

        return stats;
    }

    // ==================== NOTIFICATIONS ====================

    public PageResponse<NotificationDTO> getNotifications(String email, Pageable pageable) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        Page<NotificationEntity> notificationPage = notificationRepository
                .findByUserIdAndUserRoleOrderByCreatedAtDesc(nurse.getId(), UserRole.NURSE, pageable);

        List<NotificationDTO> notifications = notificationPage.getContent().stream()
                .map(this::convertToNotificationDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                notifications,
                notificationPage.getNumber(),
                notificationPage.getSize(),
                notificationPage.getTotalElements(),
                notificationPage.getTotalPages(),
                notificationPage.isLast()
        );
    }

    @Transactional
    public void markNotificationAsRead(String email, Long notificationId) {
        NurseEntity nurse = nurseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        NotificationEntity notification = notificationRepository
                .findByIdAndUserIdAndUserRole(notificationId, nurse.getId(), UserRole.NURSE)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    // ==================== HELPER METHODS ====================

    private NurseDTO convertToDTO(NurseEntity nurse) {
        NurseDTO dto = new NurseDTO();
        dto.setId(nurse.getId());
        dto.setFullName(nurse.getFullName());
        dto.setMobileNo(nurse.getMobileNo());
        dto.setEmail(nurse.getEmail());
        dto.setProfileImage1(nurse.getProfileImage1());
        dto.setProfileImage2(nurse.getProfileImage2());
        dto.setEducationalQualification(nurse.getEducationalQualification());
        dto.setYearsOfExperience(nurse.getYearsOfExperience());
        dto.setAge(nurse.getAge());
        dto.setSpecializations(nurse.getSpecializations());
        dto.setStatus(nurse.getStatus());
        dto.setIsActive(nurse.getIsActive());
        dto.setCreatedAt(nurse.getCreatedAt());

        Double avgRating = reviewRepository.calculateAverageRating(nurse);
        dto.setRating(avgRating != null ? avgRating : 0.0);
        dto.setTotalReviews(reviewRepository.countByNurse(nurse).intValue());

        return dto;
    }

    private AvailabilityScheduleDTO convertToScheduleDTO(AvailabilityScheduleEntity schedule) {
        return new AvailabilityScheduleDTO(
                schedule.getId(),
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getIsActive(),
                schedule.getCreatedAt()
        );
    }

    private BookingDTO convertToBookingDTO(BookingEntity booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setPatientId(booking.getPatient().getId());
        dto.setPatientName(booking.getPatient().getFullName());
        dto.setPatientEmail(booking.getPatient().getEmail());
        dto.setPatientMobile(booking.getPatient().getMobileNo());
        dto.setNurseId(booking.getNurse().getId());
        dto.setNurseName(booking.getNurse().getFullName());
        dto.setNurseEmail(booking.getNurse().getEmail());
        dto.setNurseMobile(booking.getNurse().getMobileNo());
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setBaseCharge(booking.getBaseCharge());
        dto.setSubscriptionCharge(booking.getSubscriptionCharge());
        dto.setStatus(booking.getStatus());
        dto.setPatientNotes(booking.getPatientNotes());
        dto.setNurseNotes(booking.getNurseNotes());
        dto.setCareReport(booking.getCareReport());
        dto.setAcceptedAt(booking.getAcceptedAt());
        dto.setRejectedAt(booking.getRejectedAt());
        dto.setCompletedAt(booking.getCompletedAt());
        dto.setRejectionReason(booking.getRejectionReason());
        dto.setCreatedAt(booking.getCreatedAt());
        return dto;
    }

    private PageResponse<BookingDTO> convertToBookingPageResponse(Page<BookingEntity> bookingPage) {
        List<BookingDTO> bookings = bookingPage.getContent().stream()
                .map(this::convertToBookingDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                bookings,
                bookingPage.getNumber(),
                bookingPage.getSize(),
                bookingPage.getTotalElements(),
                bookingPage.getTotalPages(),
                bookingPage.isLast()
        );
    }

    private ReviewDTO convertToReviewDTO(ReviewEntity review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setBookingId(review.getBooking().getId());
        dto.setPatientId(review.getPatient().getId());
        dto.setPatientName(review.getPatient().getFullName());
        dto.setNurseId(review.getNurse().getId());
        dto.setNurseName(review.getNurse().getFullName());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setNurseReply(review.getNurseReply());
        dto.setRepliedAt(review.getRepliedAt());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }

    private NotificationDTO convertToNotificationDTO(NotificationEntity notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getRelatedEntityId(),
                notification.getIsRead(),
                notification.getReadAt(),
                notification.getCreatedAt()
        );
    }
}