package com.hotel.reservation.domain.booking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BookedRoomResponseWithImage {
    @JsonIgnore
    private Long bookingId;
    private List<String> storeS3Urls;
    private Long roomInfoId;
    private String roomType;
    private int bookingPrice;
    private int count;
    private LocalDateTime date;

    public BookedRoomResponseWithImage() {
    }

    @QueryProjection
    public BookedRoomResponseWithImage(Long bookingId, Long roomInfoId, String roomType, int bookingPrice, int count, LocalDateTime date) {
        this.bookingId = bookingId;
        this.roomInfoId = roomInfoId;
        this.roomType = roomType;
        this.bookingPrice = bookingPrice;
        this.count = count;
        this.date = date;
    }
}
