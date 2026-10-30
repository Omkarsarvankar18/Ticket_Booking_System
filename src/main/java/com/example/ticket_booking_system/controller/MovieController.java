package com.example.ticket_booking_system.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ticket_booking_system.dto.MovieRequestDTO;
import com.example.ticket_booking_system.dto.MovieResponseDTO;
import com.example.ticket_booking_system.entity.Movie;
import com.example.ticket_booking_system.service.MovieService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies")
@Validated

public class MovieController {
	
	private final MovieService movieService;
	@PostMapping
	public ResponseEntity<MovieResponseDTO> createMovie(@Valid @RequestBody MovieRequestDTO movieRequestDTO){
		return new ResponseEntity<>(movieService.createMovie(movieRequestDTO),HttpStatus.CREATED);
	}
	
	
	//To retrieve the List of All Movies
	@GetMapping
	public ResponseEntity<List<MovieResponseDTO>> getAllMovie(){
		return new ResponseEntity<List<MovieResponseDTO>>(movieService.getAllMovies(),HttpStatus.OK);
	}
	
	//to get a movie by Id
	@GetMapping("/{id}")
	public ResponseEntity<MovieResponseDTO> getMovieById(@PathVariable Long id){
		return new ResponseEntity<MovieResponseDTO>(movieService.findMovieByID(id), HttpStatus.OK);
	}
	
	//to get a movie by Title
	@GetMapping("/title/{title}")
	public ResponseEntity<List<MovieResponseDTO>> getMovieByTitle(@RequestParam String title){
		List<MovieResponseDTO> movie = movieService.findMovieByTitle(title);
		return new ResponseEntity<>(movie, HttpStatus.OK);
		
	}
	//to get a movie by Genre
	@GetMapping("/genre/{genre}")
	public ResponseEntity<List<MovieResponseDTO>> getMovieByGenre(@RequestParam String genre){
		List<MovieResponseDTO> movie = movieService.findMovieByGenre(genre);
		return new ResponseEntity<>(movie, HttpStatus.OK);
		
	}
	//to update a movie by Id
	@PutMapping("{id}")
	public ResponseEntity<MovieResponseDTO> updateMovie(@PathVariable Long id,  @RequestBody MovieRequestDTO movieRequestDTO){
		return new ResponseEntity<>(movieService.updateMovieById(id, movieRequestDTO),HttpStatus.OK);
	}
	
	//delete the movie by id
	@DeleteMapping("/{id}")
	public ResponseEntity<Movie> deleteMovie(@PathVariable Long id){
		 movieService.deleteMovieById(id);
		return new ResponseEntity<Movie>(HttpStatus.OK);
	}
	
	
	

}
