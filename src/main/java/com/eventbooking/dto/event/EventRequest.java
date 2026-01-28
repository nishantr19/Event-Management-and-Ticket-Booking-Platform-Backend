package com.eventbooking.dto.event;

import com.eventbooking.enums.EventCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EventRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
    private String description;

    @NotNull(message = "Category is required")
    private EventCategory category;

    @NotBlank(message = "Venue is required")
    private String venue;

    @NotBlank(message = "City is required")
    private String city;

    @NotNull(message = "Event date is required")
    @Future(message = "Event date must be in the future")
    private LocalDateTime eventDate;

    @NotNull(message = "Ticket price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Ticket price must be positive")
    private BigDecimal ticketPrice;

    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "Total seats must be at least 1")
    private Integer totalSeats;

    private String imageUrl;
}