package com.example.ticket_booking_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ticket_booking_system.entity.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {

	public List<Movie> findByTitle(String title);
	
	public List<Movie> findByGenre(String genre);
	
}
