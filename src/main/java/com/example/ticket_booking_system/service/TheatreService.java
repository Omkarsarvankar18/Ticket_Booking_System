package com.example.ticket_booking_system.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.ticket_booking_system.dto.TheatreRequestDTO;
import com.example.ticket_booking_system.dto.TheatreResponseDTO;
import com.example.ticket_booking_system.entity.Theatre;
import com.example.ticket_booking_system.repository.TheatreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TheatreService {
	
	private final TheatreRepository theatreRepository;
	private final ModelMapper modelMapper;
	
	public List<TheatreResponseDTO> getAllTheatre(){
		List<Theatre> theatres = theatreRepository.findAll();
		List<TheatreResponseDTO> theatreResponseDTOs = new ArrayList(); 
		
		for(Theatre theatre:theatres) {
		 theatreResponseDTOs.add(modelMapper.map(theatre, TheatreResponseDTO.class));	
		}
		return theatreResponseDTOs;
	
	}
	
	public TheatreResponseDTO addTheatre(TheatreRequestDTO theatreRequestDTO) {
		Theatre theatre = modelMapper.map(theatreRequestDTO, Theatre.class);
		Theatre savedTheatre = theatreRepository.save(theatre);
		return modelMapper.map(savedTheatre, TheatreResponseDTO.class);
		
		
	}

}
