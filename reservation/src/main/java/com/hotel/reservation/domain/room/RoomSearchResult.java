package com.hotel.reservation.domain.room;

import com.hotel.reservation.domain.roomInfoImage.RoomInfoImage;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoomSearchResult {
    private Long roomInfoId;
    private String roomType;
    private int price;
    private int quantity;
    private Long roomCount;
    private List<String> storeS3Urls;

    public RoomSearchResult() {
    }

    @QueryProjection
    public RoomSearchResult(Long roomInfoId, String roomType, int price, int quantity, Long roomCount) {
        this.roomInfoId = roomInfoId;
        this.roomType = roomType;
        this.price = price;
        this.quantity = quantity;
        this.roomCount = roomCount;
    }
}
