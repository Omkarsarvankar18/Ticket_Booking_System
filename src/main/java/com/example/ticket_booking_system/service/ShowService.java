
package com.example.ticket_booking_system.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ticket_booking_system.dto.ShowRequestDTO;
import com.example.ticket_booking_system.dto.ShowResponseDTO;
import com.example.ticket_booking_system.entity.Movie;
import com.example.ticket_booking_system.entity.Screen;
import com.example.ticket_booking_system.entity.Seat;
import com.example.ticket_booking_system.entity.Show;
import com.example.ticket_booking_system.entity.ShowSeat;
import com.example.ticket_booking_system.enums.SeatStatus;
import com.example.ticket_booking_system.enums.ShowStatus;
import com.example.ticket_booking_system.repository.MovieRepository;
import com.example.ticket_booking_system.repository.ScreenRepository;
import com.example.ticket_booking_system.repository.SeatRepository;
import com.example.ticket_booking_system.repository.ShowRepository;
import com.example.ticket_booking_system.repository.ShowSeatRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final ShowSeatRepository showSeatRepository;
    private final ModelMapper modelMapper;

    // Get all shows
    public List<ShowResponseDTO> getAllShows() {

        List<Show> shows = showRepository.findAll();

        List<ShowResponseDTO> showResponseDTOs = new ArrayList<>();

        for (Show show : shows) {
            showResponseDTOs.add(
                    modelMapper.map(show, ShowResponseDTO.class)
            );
        }

        return showResponseDTOs;
    }

    // Get show by ID
    public ShowResponseDTO getShowById(Long id) {

        Show show = showRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Show not found with ID: " + id)
                );

        return modelMapper.map(show, ShowResponseDTO.class);
    }

    // Add new show and automatically generate ShowSeats
    @Transactional
    public ShowResponseDTO addShows(ShowRequestDTO showRequestDTO) {

        // Find movie
        Movie movie = movieRepository.findById(showRequestDTO.getMovieId())
                .orElseThrow(() ->
                        new RuntimeException("Movie not found")
                );

        // Find screen
        Screen screen = screenRepository.findById(showRequestDTO.getScreenId())
                .orElseThrow(() ->
                        new RuntimeException("Screen not found")
                );

        // Validate show timing
        if (!showRequestDTO.getEndTime()
                .isAfter(showRequestDTO.getStartTime())) {

            throw new IllegalArgumentException(
                    "End time must be after start time"
            );
        }

        // Get all physical seats of the selected screen
        List<Seat> seats = seatRepository.findByScreenId(screen.getId());

        if (seats.isEmpty()) {
            throw new IllegalArgumentException(
                    "No physical seats found for this screen. Add seats first."
            );
        }

        // Create Show entity
        Show show = new Show();

        show.setMovie(movie);
        show.setScreen(screen);
        show.setStartTime(showRequestDTO.getStartTime());
        show.setEndTime(showRequestDTO.getEndTime());

        BigDecimal basePrice =
                BigDecimal.valueOf(showRequestDTO.getBasePrice());

        show.setBasePrice(basePrice);
        show.setStatus(ShowStatus.SCHEDULED);

        // Save show first
        Show savedShow = showRepository.save(show);

        // Generate ShowSeat records for every physical seat
        List<ShowSeat> showSeats = new ArrayList<>();

        for (Seat seat : seats) {

            ShowSeat showSeat = new ShowSeat();

            showSeat.setShow(savedShow);
            showSeat.setSeat(seat);
            showSeat.setPrice(basePrice);
            showSeat.setStatus(SeatStatus.AVAILABLE);

            showSeats.add(showSeat);
        }

        // Save all show seats together
        showSeatRepository.saveAll(showSeats);

        // Return response
        return modelMapper.map(savedShow, ShowResponseDTO.class);
    }
}
