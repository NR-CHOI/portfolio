package com.hotel.reservation.web.roomInfo.form;

import com.hotel.reservation.domain.roomInfoImage.RoomInfoImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RoomInfoViewForm {
    private Long roomInfoId;
    private Integer roomNo;
    private String roomType;
    private int maxNum;
    private List<RoomInfoImage> imageFiles;
}
