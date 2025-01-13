package com.hotel.reservation.domain.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MyReservationCond {
    private Long id;
    private BookingStatus status;
    private Long bookingId;

}
