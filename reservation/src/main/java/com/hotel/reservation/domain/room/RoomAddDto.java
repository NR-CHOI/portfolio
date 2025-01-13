package com.hotel.reservation.domain.room;

import com.hotel.reservation.domain.roomInfo.RoomInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class RoomAddDto {
    private LocalDateTime date;
    private int price;
    private int quantity;
    private Boolean status;
    private RoomInfo roomInfo;
}
