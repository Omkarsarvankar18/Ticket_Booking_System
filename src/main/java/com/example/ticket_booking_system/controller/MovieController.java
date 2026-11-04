
package com.example.ticket_booking_system.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    // Add movie
    @PostMapping
    public ResponseEntity<MovieResponseDTO> createMovie(
            @Valid @RequestBody MovieRequestDTO movieRequestDTO) {

        return new ResponseEntity<>(
                movieService.createMovie(movieRequestDTO),
                HttpStatus.CREATED
        );
    }

    // Get all movies
    @GetMapping
    public ResponseEntity<List<MovieResponseDTO>> getAllMovie() {

        return ResponseEntity.ok(movieService.getAllMovies());
    }

    // Get movie by ID
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> getMovieById(
            @PathVariable Long id) {

        return ResponseEntity.ok(movieService.findMovieByID(id));
    }

    // Get movie by title
    @GetMapping("/title/{title}")
    public ResponseEntity<List<MovieResponseDTO>> getMovieByTitle(
            @PathVariable String title) {

        return ResponseEntity.ok(movieService.findMovieByTitle(title));
    }

    // Get movie by genre
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<MovieResponseDTO>> getMovieByGenre(
            @PathVariable String genre) {

        return ResponseEntity.ok(movieService.findMovieByGenre(genre));
    }

    // Update movie
    @PutMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody MovieRequestDTO movieRequestDTO) {

        return ResponseEntity.ok(
                movieService.updateMovieById(id, movieRequestDTO)
        );
    }

    // Delete movie
    @DeleteMapping("/{id}")
    public ResponseEntity<Movie> deleteMovie(@PathVariable Long id) {

        movieService.deleteMovieById(id);

        return ResponseEntity.noContent().build();
    }
}
