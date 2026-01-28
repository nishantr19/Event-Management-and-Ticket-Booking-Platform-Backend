package com.eventbooking.dto.event;

import com.eventbooking.enums.EventCategory;
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
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private EventCategory category;
    private String venue;
    private String city;
    private LocalDateTime eventDate;
    private BigDecimal ticketPrice;
    private Integer totalSeats;
    private Integer availableSeats;
    private String imageUrl;
    private Boolean active;
    private LocalDateTime createdAt;
}