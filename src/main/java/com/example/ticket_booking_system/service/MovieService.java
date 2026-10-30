package com.example.ticket_booking_system.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.ticket_booking_system.dto.MovieRequestDTO;
import com.example.ticket_booking_system.dto.MovieResponseDTO;
import com.example.ticket_booking_system.entity.Movie;
import com.example.ticket_booking_system.enums.Status;
import com.example.ticket_booking_system.exception.ResourcesNotFound;
import com.example.ticket_booking_system.repository.MovieRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
//this is used to create a constructor injection for final 
public class MovieService {

	private final MovieRepository movieRepository;

	private final ModelMapper modelMapper;

	// add the movie to the database via DTO
	public MovieResponseDTO createMovie(MovieRequestDTO movieRequestDTO) {
		Movie movie = modelMapper.map(movieRequestDTO, Movie.class);
		movie.setStatus(Status.ACTIVE);
		Movie savedMovie = movieRepository.save(movie);
		return modelMapper.map(savedMovie, MovieResponseDTO.class);
	}

	// List of all Movies
	public List<MovieResponseDTO> getAllMovies() {
		List<Movie> movies = movieRepository.findAll();
		List<MovieResponseDTO> movieResponseDTOs = new ArrayList<>();

		for (Movie movie : movies) {
			movieResponseDTOs.add(modelMapper.map(movie, MovieResponseDTO.class));
		}
		return movieResponseDTOs;
	}

	// find movie by id
	public MovieResponseDTO findMovieByID(Long id) {
		Movie movie = movieRepository.findById(id)
				.orElseThrow(() -> new ResourcesNotFound("Movie id " + id + " is not Found"));

		return modelMapper.map(movie, MovieResponseDTO.class);
	}

	// find Movie by title
	public List<MovieResponseDTO> findMovieByTitle(String title) {
		List<Movie> movies = movieRepository.findByTitle(title);
		List<MovieResponseDTO> movieResponseDTOs = new ArrayList<MovieResponseDTO>();
		for(Movie movie:movies) {
			movieResponseDTOs.add(modelMapper.map(movie, MovieResponseDTO.class));
		}
		return movieResponseDTOs;
	}
	
	// find Movie by Genre
		public List<MovieResponseDTO> findMovieByGenre(String genre) {
			List<Movie> movies = movieRepository.findByGenre(genre);
			List<MovieResponseDTO> movieResponseDTOs = new ArrayList<MovieResponseDTO>();
			for(Movie movie:movies) {
				movieResponseDTOs.add(modelMapper.map(movie, MovieResponseDTO.class));
			}
			return movieResponseDTOs;
		}

	//update the movie
	public MovieResponseDTO updateMovieById(Long id, MovieRequestDTO movieRequestDTO) {
		
		Movie movie = movieRepository.findById(id)
				.orElseThrow(() -> new ResourcesNotFound("Movie id " + id + " is not Found"));
		
		     modelMapper.map(movieRequestDTO, movie);	
		     
		Movie updatedMovie = movieRepository.save(movie);
		
		return modelMapper.map(updatedMovie, MovieResponseDTO.class);
	}
	
	//To delete the movie
	public void deleteMovieById(Long id) {
		movieRepository.deleteById(id);
		
	}
}
