package com.example.ticket_booking_system.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.ticket_booking_system.entity.Booking;
import com.example.ticket_booking_system.enums.BookingStatus;
import com.example.ticket_booking_system.repository.BookingRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingScheduler {

    private final BookingRepository bookingRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void completeBookingsForFinishedShows() {

        List<Booking> bookings =
                bookingRepository.findBookingsForCompletedShows(
                        BookingStatus.CONFIRMED,
                        LocalDateTime.now());

        for (Booking booking : bookings) {
            booking.setStatus(BookingStatus.COMPLETED);
        }

        bookingRepository.saveAll(bookings);
    }
}