package com.eventbooking.controller;

import com.eventbooking.dto.event.EventRequest;
import com.eventbooking.dto.event.EventResponse;
import com.eventbooking.enums.EventCategory;
import com.eventbooking.service.EventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Admin Only - Create Event
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        EventResponse response = eventService.createEvent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Admin Only - Update Event
    @PutMapping("/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequest request) {
        EventResponse response = eventService.updateEvent(eventId, request);
        return ResponseEntity.ok(response);
    }

    // Admin Only - Delete Event (soft delete)
    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok("Event deleted successfully");
    }

    // Public - Get Event by ID
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long eventId) {
        EventResponse response = eventService.getEventById(eventId);
        return ResponseEntity.ok(response);
    }

    // Public - Get All Events (with pagination)
    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        Page<EventResponse> events = eventService.getAllEvents(page, size, sortBy);
        return ResponseEntity.ok(events);
    }

    // Public - Get Upcoming Events
    @GetMapping("/upcoming")
    public ResponseEntity<Page<EventResponse>> getUpcomingEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<EventResponse> events = eventService.getUpcomingEvents(page, size);
        return ResponseEntity.ok(events);
    }

    // Public - Get Events by Category
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<EventResponse>> getEventsByCategory(
            @PathVariable EventCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<EventResponse> events = eventService.getEventsByCategory(category, page, size);
        return ResponseEntity.ok(events);
    }

    // Public - Get Events by City
    @GetMapping("/city/{city}")
    public ResponseEntity<Page<EventResponse>> getEventsByCity(
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<EventResponse> events = eventService.getEventsByCity(city, page, size);
        return ResponseEntity.ok(events);
    }

    // Public - Search Events
    @GetMapping("/search")
    public ResponseEntity<Page<EventResponse>> searchEvents(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<EventResponse> events = eventService.searchEvents(keyword, page, size);
        return ResponseEntity.ok(events);
    }

    // Public - Get Events with Available Seats
    @GetMapping("/available")
    public ResponseEntity<Page<EventResponse>> getAvailableEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<EventResponse> events = eventService.getAvailableEvents(page, size);
        return ResponseEntity.ok(events);
    }
}