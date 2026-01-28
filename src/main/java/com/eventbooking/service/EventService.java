package com.eventbooking.service;

import com.eventbooking.dto.event.EventRequest;
import com.eventbooking.dto.event.EventResponse;
import com.eventbooking.entity.Event;
import com.eventbooking.enums.EventCategory;
import com.eventbooking.exception.ResourceNotFoundException;
import com.eventbooking.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public EventResponse createEvent(EventRequest request) {
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .venue(request.getVenue())
                .city(request.getCity())
                .eventDate(request.getEventDate())
                .ticketPrice(request.getTicketPrice())
                .totalSeats(request.getTotalSeats())
                .availableSeats(request.getTotalSeats())
                .imageUrl(request.getImageUrl())
                .active(true)
                .build();

        event = eventRepository.save(event);
        return mapToResponse(event);
    }

    @Transactional
    public EventResponse updateEvent(Long eventId, EventRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setVenue(request.getVenue());
        event.setCity(request.getCity());
        event.setEventDate(request.getEventDate());
        event.setTicketPrice(request.getTicketPrice());

        // Update total seats and adjust available seats proportionally
        if (!event.getTotalSeats().equals(request.getTotalSeats())) {
            int bookedSeats = event.getTotalSeats() - event.getAvailableSeats();
            event.setTotalSeats(request.getTotalSeats());
            event.setAvailableSeats(request.getTotalSeats() - bookedSeats);
        }

        event.setImageUrl(request.getImageUrl());

        event = eventRepository.save(event);
        return mapToResponse(event);
    }

    public EventResponse getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        return mapToResponse(event);
    }

    public Page<EventResponse> getAllEvents(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return eventRepository.findByActiveTrue(pageable)
                .map(this::mapToResponse);
    }

    public Page<EventResponse> getUpcomingEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return eventRepository.findUpcomingEvents(LocalDateTime.now(), pageable)
                .map(this::mapToResponse);
    }

    public Page<EventResponse> getEventsByCategory(EventCategory category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("eventDate").ascending());
        return eventRepository.findByCategoryAndActiveTrue(category, pageable)
                .map(this::mapToResponse);
    }

    public Page<EventResponse> getEventsByCity(String city, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("eventDate").ascending());
        return eventRepository.findByCityContainingIgnoreCaseAndActiveTrue(city, pageable)
                .map(this::mapToResponse);
    }

    public Page<EventResponse> searchEvents(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("eventDate").ascending());
        return eventRepository.searchByTitle(keyword, pageable)
                .map(this::mapToResponse);
    }

    public Page<EventResponse> getAvailableEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("eventDate").ascending());
        return eventRepository.findEventsWithAvailableSeats(LocalDateTime.now(), pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        event.setActive(false);
        eventRepository.save(event);
    }

    private EventResponse mapToResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .category(event.getCategory())
                .venue(event.getVenue())
                .city(event.getCity())
                .eventDate(event.getEventDate())
                .ticketPrice(event.getTicketPrice())
                .totalSeats(event.getTotalSeats())
                .availableSeats(event.getAvailableSeats())
                .imageUrl(event.getImageUrl())
                .active(event.getActive())
                .createdAt(event.getCreatedAt())
                .build();
    }
}