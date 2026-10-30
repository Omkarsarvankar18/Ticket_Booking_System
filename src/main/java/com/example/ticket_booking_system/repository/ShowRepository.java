package com.example.ticket_booking_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ticket_booking_system.entity.Show;

public interface ShowRepository extends JpaRepository<Show, Long>  {

}
