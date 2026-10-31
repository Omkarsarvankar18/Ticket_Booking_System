package com.example.ticket_booking_system.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.ticket_booking_system.security.CustomUserDetailsService;
import com.example.ticket_booking_system.security.JwtAuthenticationFilter;


@Configuration
public class SecurityConfiguration {

	@Bean
	public SecurityFilterChain securityFilterChain(
			HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) {
		
		http.cors(cors -> {});
		http.csrf(csrf -> csrf.disable());

		http.sessionManagement(session -> session
		        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

//		http.authorizeHttpRequests(auth -> auth
//
//		        .requestMatchers(
//		                "/api/v1/auth/register",
//		                "/api/v1/auth/login"
//		        ).permitAll()
//
//		        .requestMatchers(
//		                "/swagger-ui/**",
//		                "/v3/api-docs/**"
//		        ).permitAll()
//
//		        .requestMatchers(HttpMethod.GET,
//		                "/api/v1/movies",
//		                "/api/v1/movies/**"
//		        ).permitAll()
//
//		        .requestMatchers(HttpMethod.GET,
//		                "/api/v1/theatres",
//		                "/api/v1/theatres/**"
//		        ).permitAll()
//
//		        .requestMatchers(HttpMethod.GET,
//		                "/api/v1/shows",
//		                "/api/v1/shows/**"
//		        ).permitAll()
//
//		        .requestMatchers(HttpMethod.GET,
//		                "/api/v1/shows/*/seats"
//		        ).permitAll()
//
//		        .requestMatchers(HttpMethod.GET,
//		                "/api/v1/users"
//		        ).hasRole("ADMIN")
//
//		        .requestMatchers("/api/v1/users/me")
//		        .hasAnyRole("CUSTOMER", "ADMIN")
//
//		        .requestMatchers("/api/v1/movies/**")
//		        .hasRole("ADMIN")
//
//		        .requestMatchers("/api/v1/theatres/**")
//		        .hasRole("ADMIN")
//
//		        .requestMatchers("/api/v1/shows/**")
//		        .hasRole("ADMIN")
//
//		        .requestMatchers("/api/v1/bookings/**")
//		        .hasAnyRole("CUSTOMER", "ADMIN")
//
//		        .anyRequest().authenticated()
//		);

		http.addFilterBefore(jwtAuthenticationFilter, 
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(
			AuthenticationConfiguration configuration) {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider(
			PasswordEncoder passwordEncoder, 
			CustomUserDetailsService userDetailsService) {
		DaoAuthenticationProvider dao = 
				new DaoAuthenticationProvider(userDetailsService);
		dao.setPasswordEncoder(passwordEncoder);
		return dao;
	}
}