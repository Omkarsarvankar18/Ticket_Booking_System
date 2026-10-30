//package com.example.ticket_booking_system.service;
//
//import java.io.ObjectInputFilter.Status;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import org.modelmapper.ModelMapper;
//import org.springframework.stereotype.Service;
//
//import com.example.ticket_booking_system.dto.ShowRequestDTO;
//import com.example.ticket_booking_system.dto.ShowResponseDTO;
//import com.example.ticket_booking_system.entity.Show;
//import com.example.ticket_booking_system.enums.ShowStatus;
//import com.example.ticket_booking_system.repository.ShowRepository;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class ShowService {
//	private final ShowRepository showRepository;
//	private final ModelMapper modelMapper;
//
//	public List<ShowResponseDTO> getAllShows() {
//		List<Show> shows = showRepository.findAll();
//		List<ShowResponseDTO> showResponseDTOs = new ArrayList<>();
//
//		for (Show show : shows) {
//			showResponseDTOs.add(modelMapper.map(show, ShowResponseDTO.class));
//		}
//		return showResponseDTOs;
//	}
//
//	public ShowResponseDTO getShowById(Long id) {
//		Optional<Show> show = showRepository.findById(id);
//		return modelMapper.map(show, ShowResponseDTO.class);
//	}
//
//	public ShowResponseDTO addShows(ShowRequestDTO showRequestDTO) {
//	Show show =	modelMapper.map(showRequestDTO, Show.class);
//	show.setStatus(ShowStatus.SCHEDULED);
//		showRepository.save(show);
//		return modelMapper.map(show, ShowResponseDTO.class);
//	}
//
//}








package com.example.ticket_booking_system.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.ticket_booking_system.dto.ShowRequestDTO;
import com.example.ticket_booking_system.dto.ShowResponseDTO;
import com.example.ticket_booking_system.entity.Movie;
import com.example.ticket_booking_system.entity.Screen;
import com.example.ticket_booking_system.entity.Show;
import com.example.ticket_booking_system.enums.ShowStatus;
import com.example.ticket_booking_system.repository.MovieRepository;
import com.example.ticket_booking_system.repository.ScreenRepository;
import com.example.ticket_booking_system.repository.ShowRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;
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

    // Add new show
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

        // Create Show entity manually
        Show show = new Show();

        show.setMovie(movie);
        show.setScreen(screen);
        show.setStartTime(showRequestDTO.getStartTime());
        show.setEndTime(showRequestDTO.getEndTime());
        show.setBasePrice(
                BigDecimal.valueOf(showRequestDTO.getBasePrice())
        );

        show.setStatus(ShowStatus.SCHEDULED);

        // Save show
        Show savedShow = showRepository.save(show);

        // Convert entity into response DTO
        return modelMapper.map(savedShow, ShowResponseDTO.class);
    }
}

