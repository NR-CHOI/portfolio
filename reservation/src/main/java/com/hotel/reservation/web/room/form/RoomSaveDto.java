package com.hotel.reservation.web.room.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class RoomSaveDto {
    private Long roomInfoId;
    private int year;
    private int month;
    private int day;

}
