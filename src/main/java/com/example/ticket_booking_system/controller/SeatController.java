package com.example.ticket_booking_system.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ticket_booking_system.dto.SeatRequestDTO;
import com.example.ticket_booking_system.dto.SeatResponseDTO;
import com.example.ticket_booking_system.service.SeatService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class SeatController {
	
	private final SeatService seatService;
	
	@PostMapping("/screens/{screenId}/seats")
	public ResponseEntity<SeatResponseDTO> addSeat(@PathVariable @Positive Long screenId, @Valid @RequestBody SeatRequestDTO seatRequestDTO){
	SeatResponseDTO response =seatService.addSeat(screenId, seatRequestDTO);
	return new ResponseEntity<>(response,HttpStatus.CREATED);
	}
	
	public ResponseEntity<List<SeatResponseDTO>> getSeatByScreenId(@PathVariable @Positive Long screenId){
		List<SeatResponseDTO> seats =seatService.getSeatsByScreenId(screenId);
		return ResponseEntity.ok(seats);
	}

}
