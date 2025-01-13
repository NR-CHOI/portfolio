package com.hotel.reservation.web.roomInfo.form;

import com.hotel.reservation.domain.roomInfoImage.RoomInfoImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class RoomInfoUpdateForm {
    @NotNull
    private Integer roomNo;
    @NotBlank
    private String roomType;
    @NotNull
    private int maxNum;
    private List<RoomInfoImage> imageFiles;
}
