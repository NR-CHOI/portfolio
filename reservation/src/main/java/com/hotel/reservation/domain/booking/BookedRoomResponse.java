package com.hotel.reservation.domain.booking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookedRoomResponse {
    @JsonIgnore
    private Long bookingId;
    private String roomType;
    private int bookingPrice;
    private int count;
    private LocalDateTime date;

    public BookedRoomResponse() {
    }

    @QueryProjection
    public BookedRoomResponse(Long bookingId, String roomType, int bookingPrice, int count, LocalDateTime date) {
        this.bookingId = bookingId;
        this.roomType = roomType;
        this.bookingPrice = bookingPrice;
        this.count = count;
        this.date = date;
    }
}
