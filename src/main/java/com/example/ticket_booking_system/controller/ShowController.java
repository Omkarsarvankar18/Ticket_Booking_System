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

import com.example.ticket_booking_system.dto.ShowRequestDTO;
import com.example.ticket_booking_system.dto.ShowResponseDTO;
import com.example.ticket_booking_system.entity.Show;
import com.example.ticket_booking_system.service.ShowService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shows")
public class ShowController {
	
	private final ShowService showService;
	
	@GetMapping
	public ResponseEntity<List<ShowResponseDTO>> getAllShows(){
		return new ResponseEntity<>(showService.getAllShows(),HttpStatus.OK);
	}
	
	@PostMapping("/{id}")
	public ResponseEntity<ShowResponseDTO> getShowById(@PathVariable Long id){
		return new ResponseEntity<>(showService.getShowById(id), HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<ShowResponseDTO> addShow(@RequestBody ShowRequestDTO showRequestDTO){
		return new ResponseEntity<ShowResponseDTO>(showService.addShows(showRequestDTO), HttpStatus.OK);
	}

}
