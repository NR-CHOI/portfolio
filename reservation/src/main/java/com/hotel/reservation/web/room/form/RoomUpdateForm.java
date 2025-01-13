package com.hotel.reservation.web.room.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomUpdateForm {
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @NotNull
    private int price;
    @NotNull
    private int quantity;
    private Boolean status;
    @NotNull
    private Long roomInfoId;//roomInfo_id

    public RoomUpdateForm(LocalDate startDate, Long roomInfoId) {
        this.startDate = startDate;
        this.roomInfoId = roomInfoId;
    }
}
