package com.hotel.reservation.web.roomInfo.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
public class RoomInfoSaveRequest {
    @NotNull
    private Integer roomNo;
    @NotBlank
    private String roomType;
    @NotNull
    private Integer maxNum;
    private Long id;//memberId
    private List<MultipartFile> imageFiles;
}
