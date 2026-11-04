
package com.example.ticket_booking_system.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ticket_booking_system.dto.BookingRequestDTO;
import com.example.ticket_booking_system.dto.BookingResponseDTO;
import com.example.ticket_booking_system.entity.Booking;
import com.example.ticket_booking_system.entity.BookingSeat;
import com.example.ticket_booking_system.entity.Show;
import com.example.ticket_booking_system.entity.ShowSeat;
import com.example.ticket_booking_system.entity.User;
import com.example.ticket_booking_system.enums.BookingStatus;
import com.example.ticket_booking_system.enums.SeatStatus;
import com.example.ticket_booking_system.enums.ShowStatus;
import com.example.ticket_booking_system.exception.ResourcesNotFound;
import com.example.ticket_booking_system.repository.BookingRepository;
import com.example.ticket_booking_system.repository.BookingSeatRepository;
import com.example.ticket_booking_system.repository.ShowRepository;
import com.example.ticket_booking_system.repository.ShowSeatRepository;
import com.example.ticket_booking_system.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final ModelMapper modelMapper;

    // ==========================================
    // CREATE BOOKING
    // ==========================================

    @Transactional
    public BookingResponseDTO createBooking(
            Long userId,
            BookingRequestDTO bookingRequestDTO) {

        // Find customer
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourcesNotFound(
                        "User not found with ID: " + userId));

        // Find show
        Long showId = bookingRequestDTO.getShowId();

        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourcesNotFound(
                        "Show not found with ID: " + showId));

        // Check show status
        if (show.getStatus() != ShowStatus.SCHEDULED) {
            throw new IllegalArgumentException(
                    "This show is not available for booking");
        }

        // Validate selected seats
        List<ShowSeat> showSeats = new ArrayList<>();

        for (Long showSeatId : bookingRequestDTO.getShowSeatIds()) {

            ShowSeat showSeat = showSeatRepository.findById(showSeatId)
                    .orElseThrow(() -> new ResourcesNotFound(
                            "Show seat not found with ID: " + showSeatId));

            // Check whether seat belongs to selected show
            if (!showSeat.getShow().getId().equals(showId)) {
                throw new IllegalArgumentException(
                        "Selected seat does not belong to this show");
            }

            // Check seat availability
            if (showSeat.getStatus() != SeatStatus.AVAILABLE) {
                throw new IllegalArgumentException(
                        "Seat " + showSeat.getSeat().getSeatNumber()
                                + " is not available");
            }

            showSeats.add(showSeat);
        }

        // Calculate total amount
        BigDecimal totalAmount = showSeats.stream()
                .map(ShowSeat::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create booking
        Booking booking = new Booking();

        booking.setCustomer(user);
        booking.setShow(show);
        booking.setTotalAmount(totalAmount);
        booking.setBookingDateTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.CONFIRMED);

        // Generate booking reference
        booking.setBookingReference(
                "BK-" + UUID.randomUUID()
                        .toString().substring(0, 8).toUpperCase());

        Booking savedBooking = bookingRepository.save(booking);

        // Create booking seats
        List<BookingSeat> bookingSeats = new ArrayList<>();

        for (ShowSeat showSeat : showSeats) {

            BookingSeat bookingSeat = new BookingSeat();

            bookingSeat.setBooking(savedBooking);
            bookingSeat.setShowSeat(showSeat);
            bookingSeat.setPriceAtBooking(showSeat.getPrice());

            bookingSeats.add(bookingSeat);

            // Update seat status
            showSeat.setStatus(SeatStatus.BOOKED);
        }

        // Save booking seats
        bookingSeatRepository.saveAll(bookingSeats);
        showSeatRepository.saveAll(showSeats);

        // Convert entity to DTO
        BookingResponseDTO responseDTO =
                modelMapper.map(savedBooking, BookingResponseDTO.class);

        responseDTO.setCustomerId(user.getId());
        responseDTO.setCustomerName(user.getName());
        responseDTO.setShowId(show.getId());

        return responseDTO;
    }

    // ==========================================
    // GET BOOKINGS BY CUSTOMER ID
    // ==========================================

    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsByUserId(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourcesNotFound(
                        "User not found with ID: " + userId));

        List<Booking> bookings =
                bookingRepository.findByCustomer_IdOrderByBookingDateTimeDesc(userId);

        return bookings.stream().map(booking -> {

            BookingResponseDTO responseDTO =
                    modelMapper.map(booking, BookingResponseDTO.class);

            responseDTO.setCustomerId(user.getId());
            responseDTO.setCustomerName(user.getName());

            Show show = booking.getShow();

            responseDTO.setShowId(show.getId());
            responseDTO.setMovieTitle(show.getMovie().getTitle());

            responseDTO.setTheatreName(
                    show.getScreen().getTheatre().getName());

            responseDTO.setScreenName(
                    show.getScreen().getScreenName());

            responseDTO.setShowStartTime(show.getStartTime());

            List<BookingSeat> bookingSeats =
                    bookingSeatRepository.findByBookingId(booking.getId());

            List<String> seatNumbers = bookingSeats.stream()
                    .map(bookingSeat ->
                            bookingSeat.getShowSeat()
                                    .getSeat().getSeatNumber())
                    .collect(Collectors.toList());

            responseDTO.setSeatNumbers(seatNumbers);

            return responseDTO;

        }).collect(Collectors.toList());
    }

    // ==========================================
    // ADMIN: GET ALL BOOKINGS
    // ==========================================

    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getAllBookings() {

        List<Booking> bookings = bookingRepository.findAll(
                Sort.by(Sort.Direction.DESC, "bookingDateTime"));

        return bookings.stream().map(booking -> {

            BookingResponseDTO responseDTO =
                    modelMapper.map(booking, BookingResponseDTO.class);

            // Customer details
            User user = booking.getCustomer();

            responseDTO.setCustomerId(user.getId());
            responseDTO.setCustomerName(user.getName());

            // Show details
            Show show = booking.getShow();

            responseDTO.setShowId(show.getId());
            responseDTO.setMovieTitle(show.getMovie().getTitle());

            responseDTO.setTheatreName(
                    show.getScreen().getTheatre().getName());

            responseDTO.setScreenName(
                    show.getScreen().getScreenName());

            responseDTO.setShowStartTime(show.getStartTime());

            // Booked seats
            List<BookingSeat> bookingSeats =
                    bookingSeatRepository.findByBookingId(booking.getId());

            List<String> seatNumbers = bookingSeats.stream()
                    .map(bookingSeat ->
                            bookingSeat.getShowSeat()
                                    .getSeat().getSeatNumber())
                    .collect(Collectors.toList());

            responseDTO.setSeatNumbers(seatNumbers);

            return responseDTO;

        }).collect(Collectors.toList());
    }
}
