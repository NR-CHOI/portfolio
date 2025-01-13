package com.hotel.reservation.domain.room;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RoomByMemberDto {
    private Long roomId;
    private String roomType;
    private LocalDateTime date;
    private int price;
    private int quantity;
    private Boolean status;
}
