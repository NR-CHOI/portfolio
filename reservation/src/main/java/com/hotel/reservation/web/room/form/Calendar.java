package com.hotel.reservation.web.room.form;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
@AllArgsConstructor
@Getter
public class Calendar {
    private int year;
    private int month;
    private int day;
    private List<RoomCalendarList> rooms;
}
