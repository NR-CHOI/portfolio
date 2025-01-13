package com.hotel.reservation.domain.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BookingDateDto {
    private LocalDateTime arrivalDate;
    private LocalDateTime departureDate;
}
