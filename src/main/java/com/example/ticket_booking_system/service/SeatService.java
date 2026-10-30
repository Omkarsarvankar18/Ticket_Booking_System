package com.example.ticket_booking_system.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.ticket_booking_system.dto.SeatRequestDTO;
import com.example.ticket_booking_system.dto.SeatResponseDTO;
import com.example.ticket_booking_system.entity.Screen;
import com.example.ticket_booking_system.entity.Seat;
import com.example.ticket_booking_system.exception.ResourcesNotFound;
import com.example.ticket_booking_system.repository.ScreenRepository;
import com.example.ticket_booking_system.repository.SeatRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatService {

	private final SeatRepository seatRepository;
	private final ScreenRepository screenRepository;
	private final ModelMapper modelMapper;

	public SeatResponseDTO addSeat(Long screenId, SeatRequestDTO seatRequestDTO) {
		Screen screen = screenRepository.findById(screenId)
				.orElseThrow(() -> new ResourcesNotFound("Screen with id " + screenId + "Not Found"));

		Boolean exists = seatRepository.existsByScreenIdAndSeatNumber(screenId, seatRequestDTO.getSeatNumber());

		if (exists) {
			throw new IllegalArgumentException("Seat Number already exists in the screen");
		}

		Seat seat = modelMapper.map(seatRequestDTO, Seat.class);
		seat.setScreen(screen);
		Seat savedSeat = seatRepository.save(seat);

		SeatResponseDTO seatResponseDTO = modelMapper.map(savedSeat, SeatResponseDTO.class);

		seatResponseDTO.setScreenId(screen.getId());
		seatResponseDTO.setScreenName(screen.getScreenName());

		return seatResponseDTO;
	}
	
	public List<SeatResponseDTO> getSeatsByScreenId(Long screenId){
		screenRepository.findById(screenId).orElseThrow(()-> new ResourcesNotFound("Screen not Found with ID:" + screenId));
		List<Seat> seats = seatRepository.findByScreenId(screenId);
		List<SeatResponseDTO> seatResponseDTOs = new ArrayList<>();
		for(Seat seat:seats) {
			SeatResponseDTO seatResponseDTO = modelMapper.map(seat, SeatResponseDTO.class);
			seatResponseDTO.setScreenId(seat.getScreen().getId());
			seatResponseDTO.setScreenName(seat.getScreen().getScreenName());
			seatResponseDTOs.add(seatResponseDTO);
			
		}
		
		return seatResponseDTOs;
	}

}
