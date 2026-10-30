package com.example.ticket_booking_system.dto;



import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TheatreRequestDTO {

    private String name;
    private String address;
    private String city;
}

