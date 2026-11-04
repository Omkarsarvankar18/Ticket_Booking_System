
package com.example.ticket_booking_system.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.ticket_booking_system.dto.ScreenRequestDTO;
import com.example.ticket_booking_system.dto.ScreenResponseDTO;
import com.example.ticket_booking_system.entity.Screen;
import com.example.ticket_booking_system.entity.Theatre;
import com.example.ticket_booking_system.exception.ResourcesNotFound;
import com.example.ticket_booking_system.repository.ScreenRepository;
import com.example.ticket_booking_system.repository.TheatreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScreenService {

    private final ScreenRepository screenRepository;
    private final TheatreRepository theatreRepository;
    private final ModelMapper modelMapper;

    // Add Screen
    public ScreenResponseDTO addScreen(ScreenRequestDTO requestDTO) {

        // Find theatre using theatreId from request DTO
        Theatre theatre = theatreRepository.findById(requestDTO.getTheatreId())
                .orElseThrow(() -> new ResourcesNotFound("Theatre not found"));

        // Convert DTO into Screen entity
        Screen screen = modelMapper.map(requestDTO, Screen.class);

        // Set theatre relationship
        screen.setTheatre(theatre);

        // Save screen
        Screen savedScreen = screenRepository.save(screen);

        // Convert entity into response DTO
        return modelMapper.map(savedScreen, ScreenResponseDTO.class);
    }

    // Get Screens By Theatre ID
    public List<ScreenResponseDTO> getScreensByTheatreId(Long theatreId) {

        List<Screen> screens = screenRepository.findByTheatre_Id(theatreId);

        return screens.stream()
                .map(screen -> modelMapper.map(screen, ScreenResponseDTO.class))
                .collect(Collectors.toList());
    }
}
