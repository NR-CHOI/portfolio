package com.hotel.reservation.domain.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BookingSearchCondition {
    private Long id;
    private String name;
    private Long bookingId;
    private LocalDateTime arrivalDateFrom;
    private LocalDateTime arrivalDateUntil;
    private LocalDateTime departureDateFrom;
    private LocalDateTime departureDateUntil;
    private LocalDateTime bookingDateFrom;
    private LocalDateTime bookingDateUntil;
    private LocalDateTime stayDateFrom;
    private LocalDateTime stayDateUntil;

    public BookingSearchCondition(){}
}
