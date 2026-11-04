
package com.example.ticket_booking_system.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.ticket_booking_system.dto.ScreenRequestDTO;
import com.example.ticket_booking_system.dto.ScreenResponseDTO;
import com.example.ticket_booking_system.service.ScreenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/screens")
@RequiredArgsConstructor
public class ScreenController {

    private final ScreenService screenService;

    // Add Screen
    @PostMapping
    public ResponseEntity<ScreenResponseDTO> addScreen(
            @Valid @RequestBody ScreenRequestDTO screenRequestDTO) {

        return new ResponseEntity<>(
                screenService.addScreen(screenRequestDTO),
                HttpStatus.CREATED);
    }

    // Get All Screens By Theatre ID
    @GetMapping("/theatre/{theatreId}")
    public ResponseEntity<List<ScreenResponseDTO>> getScreensByTheatreId(
            @PathVariable Long theatreId) {

        return new ResponseEntity<>(
                screenService.getScreensByTheatreId(theatreId),
                HttpStatus.OK);
    }
}
