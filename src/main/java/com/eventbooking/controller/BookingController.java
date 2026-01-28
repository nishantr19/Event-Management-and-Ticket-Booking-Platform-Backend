package com.eventbooking.controller;

import com.eventbooking.dto.booking.BookingRequest;
import com.eventbooking.dto.booking.BookingResponse;
import com.eventbooking.entity.User;
import com.eventbooking.service.AuthService;
import com.eventbooking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final AuthService authService;

    public BookingController(BookingService bookingService, AuthService authService) {
        this.bookingService = bookingService;
        this.authService = authService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        User currentUser = authService.getCurrentUser();
        BookingResponse response = bookingService.createBooking(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{bookingId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long bookingId) {
        User currentUser = authService.getCurrentUser();
        BookingResponse response = bookingService.getBookingById(bookingId, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reference/{bookingReference}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookingResponse> getBookingByReference(@PathVariable String bookingReference) {
        BookingResponse response = bookingService.getBookingByReference(bookingReference);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<BookingResponse>> getMyBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User currentUser = authService.getCurrentUser();
        Page<BookingResponse> bookings = bookingService.getUserBookings(currentUser, page, size);
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/{bookingId}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long bookingId) {
        User currentUser = authService.getCurrentUser();
        BookingResponse response = bookingService.cancelBooking(bookingId, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bookingId}/qr-code")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> getQRCode(@PathVariable Long bookingId) {
        User currentUser = authService.getCurrentUser();
        BookingResponse booking = bookingService.getBookingById(bookingId, currentUser);

        if (booking.getQrCodeData() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(booking.getQrCodeData());
    }
}