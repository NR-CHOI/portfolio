package com.hotel.reservation.domain.room;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CalendarDto {
    private Long roomId;
    private LocalDateTime date;
    private String roomType;
    private int price;
    private int quantity;
    private Boolean status;

    public CalendarDto() {
    }

    @QueryProjection
    public CalendarDto(Long roomId, LocalDateTime date, String roomType, int price, int quantity, Boolean status) {
        this.roomId = roomId;
        this.date = date;
        this.roomType = roomType;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
    }
}
