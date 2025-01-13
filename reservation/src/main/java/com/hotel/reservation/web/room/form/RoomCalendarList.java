package com.hotel.reservation.web.room.form;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RoomCalendarList {
    private Long roomId;
    private String roomType;
    private int price;
    private int quantity;
    private Boolean status;
}
