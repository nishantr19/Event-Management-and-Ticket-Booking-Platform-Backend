package com.eventbooking.dto.booking;

import com.eventbooking.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String bookingReference;
    private Long userId;
    private String userEmail;
    private String userFullName;
    private Long eventId;
    private String eventTitle;
    private String eventVenue;
    private String eventCity;
    private LocalDateTime eventDate;
    private Integer numberOfSeats;
    private BigDecimal totalAmount;
    private BookingStatus status;
    private String qrCodeData;
    private LocalDateTime bookedAt;
}