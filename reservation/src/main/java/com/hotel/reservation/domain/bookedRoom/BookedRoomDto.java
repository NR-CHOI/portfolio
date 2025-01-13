package com.hotel.reservation.domain.bookedRoom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookedRoomDto {
    private int bookingPrice;
    private int count;
}
