package com.hotel.reservation.domain.room;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RoomSearchDto {

    private int quantity;
    private int people;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private Long stayDate;

    public RoomSearchDto() {
    }

    @QueryProjection
    public RoomSearchDto(int quantity, int people, LocalDateTime checkInDate, LocalDateTime checkOutDate, Long stayDate) {
        this.quantity = quantity;
        this.people = people;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.stayDate = stayDate;
    }
}