
package com.example.ticket_booking_system.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ticket_booking_system.dto.BookingRequestDTO;
import com.example.ticket_booking_system.dto.BookingResponseDTO;
import com.example.ticket_booking_system.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // Create Booking
    @PostMapping("/users/{userId}")
    public ResponseEntity<BookingResponseDTO> createBooking(
            @PathVariable Long userId,
            @Valid @RequestBody BookingRequestDTO bookingRequestDTO) {

        BookingResponseDTO responseDTO =
                bookingService.createBooking(userId, bookingRequestDTO);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    // Get all bookings of a customer
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByUserId(
            @PathVariable Long userId) {

        List<BookingResponseDTO> bookings =
                bookingService.getBookingsByUserId(userId);

        return ResponseEntity.ok(bookings);
    }
}
