package com.hotel.reservation.domain.roomInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomInfoDto {
    private Long roomInfoId;
    private String roomType;
}
