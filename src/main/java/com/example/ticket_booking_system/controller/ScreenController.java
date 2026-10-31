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

import com.example.ticket_booking_system.dto.ScreenRequestDTO;
import com.example.ticket_booking_system.dto.ScreenResponseDTO;
import com.example.ticket_booking_system.service.ScreenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/theatres/{theatreId}/screens")
@RequiredArgsConstructor
public class ScreenController {
	private final ScreenService screenService;
	
	@PostMapping
	public ResponseEntity<ScreenResponseDTO> addScreen(@PathVariable Long theatreId, @Valid @RequestBody ScreenRequestDTO screenRequestDTO){
		return new ResponseEntity<ScreenResponseDTO>(screenService.addScreen(theatreId, screenRequestDTO), HttpStatus.CREATED);
	}
	
	// Get screens by theatre ID
    @GetMapping
    public ResponseEntity<List<ScreenResponseDTO>> getScreensByTheatreId(
            @PathVariable Long theatreId) {

        return new ResponseEntity<>(
                screenService.getScreensByTheatreId(theatreId),
                HttpStatus.OK);
    }

}
