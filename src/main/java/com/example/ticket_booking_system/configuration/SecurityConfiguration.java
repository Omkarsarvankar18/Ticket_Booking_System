
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

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider) throws Exception {

        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

                // CORS preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Authentication
                .requestMatchers(
                        "/api/v1/auth/register",
                        "/api/v1/auth/login"
                ).permitAll()

                // Swagger
                .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html"
                ).permitAll()

                // Public GET APIs
                .requestMatchers(HttpMethod.GET,
                        "/api/v1/movies",
                        "/api/v1/movies/**",
                        "/api/v1/theatres",
                        "/api/v1/theatres/**",
                        "/api/v1/shows",
                        "/api/v1/shows/**",
                        "/api/v1/screens",
                        "/api/v1/screens/**"
                ).permitAll()

                // User APIs
                .requestMatchers("/api/v1/users/me")
                        .hasAnyRole("CUSTOMER", "ADMIN")

                .requestMatchers("/api/v1/users")
                        .hasRole("ADMIN")

                // Admin movie management
                .requestMatchers(HttpMethod.POST, "/api/v1/movies")
                        .hasRole("ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/v1/movies/**")
                        .hasRole("ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/api/v1/movies/**")
                        .hasRole("ADMIN")

                // Admin theatre management
                .requestMatchers(HttpMethod.POST, "/api/v1/theatres")
                        .hasRole("ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/v1/theatres/**")
                        .hasRole("ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/api/v1/theatres/**")
                        .hasRole("ADMIN")

                // Admin screen management
                .requestMatchers(HttpMethod.POST, "/api/v1/screens")
                        .hasRole("ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/v1/screens/**")
                        .hasRole("ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/api/v1/screens/**")
                        .hasRole("ADMIN")

                // Admin seat management
                .requestMatchers(HttpMethod.POST, "/api/v1/screens/*/seats")
                        .hasRole("ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/api/v1/screens/*/seats/**")
                        .hasRole("ADMIN")

                // Admin show management
                .requestMatchers(HttpMethod.POST, "/api/v1/shows")
                        .hasRole("ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/v1/shows/**")
                        .hasRole("ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/api/v1/shows/**")
                        .hasRole("ADMIN")

                // ADMIN: View all bookings
                .requestMatchers(HttpMethod.GET, "/api/v1/bookings")
                        .hasRole("ADMIN")

                // Other booking APIs
                .requestMatchers("/api/v1/bookings/**")
                        .hasAnyRole("CUSTOMER", "ADMIN")

                // Remaining APIs
                .anyRequest().authenticated()
            )

            .authenticationProvider(authenticationProvider)

            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }
}
