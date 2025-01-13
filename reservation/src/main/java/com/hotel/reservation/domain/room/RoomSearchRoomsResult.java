package com.hotel.reservation.domain.room;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class RoomSearchRoomsResult {
    private Long roomInfoId;
    private Long roomId;
    private String roomType;
    private int price;
    private int quantity;
    private LocalDateTime date;
}
