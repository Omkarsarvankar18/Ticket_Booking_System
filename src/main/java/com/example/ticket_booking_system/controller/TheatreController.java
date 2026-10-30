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

import com.example.ticket_booking_system.dto.TheatreRequestDTO;
import com.example.ticket_booking_system.dto.TheatreResponseDTO;
import com.example.ticket_booking_system.service.TheatreService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/theatres")
public class TheatreController {
	
	public final TheatreService theatreService;
	
	@GetMapping
	public ResponseEntity<List<TheatreResponseDTO>> getAllTheatre(){
		return new ResponseEntity<>(theatreService.getAllTheatre(), HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<TheatreResponseDTO> addTheatre(@Valid @RequestBody TheatreRequestDTO theatreRequestDTO){
		return new ResponseEntity<TheatreResponseDTO>(theatreService.addTheatre(theatreRequestDTO),HttpStatus.CREATED);
	}

}
