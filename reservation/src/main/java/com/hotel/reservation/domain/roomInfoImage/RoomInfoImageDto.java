package com.hotel.reservation.domain.roomInfoImage;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomInfoImageDto {
    private Long roomInfoId;
    private String storeFileName;

    public RoomInfoImageDto() {
    }

    @QueryProjection
    public RoomInfoImageDto(Long roomInfoId, String storeFileName) {
        this.roomInfoId = roomInfoId;
        this.storeFileName = storeFileName;
    }
}
