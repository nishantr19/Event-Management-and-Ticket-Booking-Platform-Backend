package com.eventbooking.repository;


import com.eventbooking.entity.Event;
import com.eventbooking.enums.EventCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findByActiveTrue(Pageable pageable);

    Page<Event> findByCategoryAndActiveTrue(EventCategory category, Pageable pageable);

    Page<Event> findByCityContainingIgnoreCaseAndActiveTrue(String city, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) AND e.active = true")
    Page<Event> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.eventDate > :now AND e.active = true ORDER BY e.eventDate ASC")
    Page<Event> findUpcomingEvents(@Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.availableSeats > 0 AND e.active = true AND e.eventDate > :now")
    Page<Event> findEventsWithAvailableSeats(@Param("now") LocalDateTime now, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.id = :id")
    Optional<Event> findByIdWithLock(@Param("id") Long id);
}