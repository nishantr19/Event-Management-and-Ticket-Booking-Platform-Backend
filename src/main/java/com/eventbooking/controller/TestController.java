package com.eventbooking.controller;


import com.eventbooking.repository.BookingRepository;
import com.eventbooking.repository.EventRepository;
import com.eventbooking.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;

    public TestController(UserRepository userRepository,
                          EventRepository eventRepository,
                          BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "Application is running!");
        status.put("userCount", userRepository.count());
        status.put("eventCount", eventRepository.count());
        status.put("bookingCount", bookingRepository.count());
        status.put("message", "All repositories are working!");
        return status;
    }
}