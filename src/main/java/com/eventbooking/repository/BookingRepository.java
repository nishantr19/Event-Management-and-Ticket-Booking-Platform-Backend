package com.eventbooking.repository;


import com.eventbooking.entity.Booking;
import com.eventbooking.entity.User;
import com.eventbooking.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingReference(String bookingReference);

    Page<Booking> findByUserOrderByBookedAtDesc(User user, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId ORDER BY b.bookedAt DESC")
    Page<Booking> findByUserIdOrderByBookedAtDesc(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.event.id = :eventId AND b.status = :status")
    Long countByEventIdAndStatus(@Param("eventId") Long eventId, @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.id = :bookingId AND b.user.id = :userId")
    Optional<Booking> findByIdAndUserId(@Param("bookingId") Long bookingId, @Param("userId") Long userId);
}