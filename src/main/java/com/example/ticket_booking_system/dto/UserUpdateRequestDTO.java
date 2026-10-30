package com.example.ticket_booking_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDTO {

	 @NotBlank(message = "Name is required")
	    private String name;

	    @NotBlank(message = "Email is required")
	    @Email(message = "Invalid email format")
	    private String email;
}
