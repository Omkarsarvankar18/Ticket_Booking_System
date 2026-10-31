
package com.example.ticket_booking_system.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.ticket_booking_system.enums.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDTO {

    private Long id;

    private Long customerId;
    private String customerName;

    private Long showId;
    private String movieTitle;
    private String theatreName;
    private String screenName;
    private LocalDateTime showStartTime;

    private List<String> seatNumbers;

    private BigDecimal totalAmount;
    private LocalDateTime bookingDateTime;

    private BookingStatus status;
    private String bookingReference;
}
