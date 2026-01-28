package com.eventbooking.service;

import com.eventbooking.dto.booking.BookingRequest;
import com.eventbooking.dto.booking.BookingResponse;
import com.eventbooking.entity.Booking;
import com.eventbooking.entity.Event;
import com.eventbooking.entity.User;
import com.eventbooking.enums.BookingStatus;
import com.eventbooking.exception.InsufficientSeatsException;
import com.eventbooking.exception.ResourceNotFoundException;
import com.eventbooking.repository.BookingRepository;
import com.eventbooking.repository.EventRepository;
import com.google.zxing.WriterException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final QRCodeService qrCodeService;

    public BookingService(BookingRepository bookingRepository,
                          EventRepository eventRepository,
                          QRCodeService qrCodeService) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.qrCodeService = qrCodeService;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request, User user) {
        // Lock the event row to prevent race conditions
        Event event = eventRepository.findByIdWithLock(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + request.getEventId()));

        // Check if event is active
        if (!event.getActive()) {
            throw new IllegalStateException("Event is not active");
        }

        // Check if enough seats are available
        if (event.getAvailableSeats() < request.getNumberOfSeats()) {
            throw new InsufficientSeatsException(
                    "Only " + event.getAvailableSeats() + " seats available, but " +
                            request.getNumberOfSeats() + " requested"
            );
        }

        // Calculate total amount
        BigDecimal totalAmount = event.getTicketPrice()
                .multiply(BigDecimal.valueOf(request.getNumberOfSeats()));

        // Generate booking reference
        String bookingReference = generateBookingReference();

        // Create booking
        Booking booking = Booking.builder()
                .bookingReference(bookingReference)
                .user(user)
                .event(event)
                .numberOfSeats(request.getNumberOfSeats())
                .totalAmount(totalAmount)
                .status(BookingStatus.CONFIRMED)
                .build();

        // Reduce available seats
        event.setAvailableSeats(event.getAvailableSeats() - request.getNumberOfSeats());
        eventRepository.save(event);

        // Save booking
        booking = bookingRepository.save(booking);

        // Generate QR Code
        try {
            String qrData = qrCodeService.generateBookingQRData(
                    booking.getId(),
                    booking.getBookingReference(),
                    event.getId(),
                    event.getTitle(),
                    user.getEmail(),
                    booking.getNumberOfSeats()
            );

            String qrCodeImage = qrCodeService.generateQRCode(qrData, 300, 300);
            booking.setQrCodeData(qrCodeImage);
            booking = bookingRepository.save(booking);
        } catch (WriterException | IOException e) {
            // If QR code generation fails, booking is still valid
            System.err.println("Failed to generate QR code: " + e.getMessage());
        }

        return mapToResponse(booking);
    }

    public BookingResponse getBookingById(Long bookingId, User user) {
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        return mapToResponse(booking);
    }

    public BookingResponse getBookingByReference(String bookingReference) {
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with reference: " + bookingReference));
        return mapToResponse(booking);
    }

    public Page<BookingResponse> getUserBookings(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookedAt").descending());
        return bookingRepository.findByUserIdOrderByBookedAtDesc(user.getId(), pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId, User user) {
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        // Release seats back to event
        Event event = booking.getEvent();
        event.setAvailableSeats(event.getAvailableSeats() + booking.getNumberOfSeats());
        eventRepository.save(event);

        // Update booking status
        booking.setStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);

        return mapToResponse(booking);
    }

    private String generateBookingReference() {
        return "BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .bookingReference(booking.getBookingReference())
                .userId(booking.getUser().getId())
                .userEmail(booking.getUser().getEmail())
                .userFullName(booking.getUser().getFullName())
                .eventId(booking.getEvent().getId())
                .eventTitle(booking.getEvent().getTitle())
                .eventVenue(booking.getEvent().getVenue())
                .eventCity(booking.getEvent().getCity())
                .eventDate(booking.getEvent().getEventDate())
                .numberOfSeats(booking.getNumberOfSeats())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .qrCodeData(booking.getQrCodeData())
                .bookedAt(booking.getBookedAt())
                .build();
    }
}