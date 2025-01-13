package com.hotel.reservation.web.roomInfo.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RoomInfoUpdateRequest {
    @NotNull
    private Integer roomNo;
    @NotBlank
    private String roomType;
    @NotNull
    private int maxNum;
    private List<MultipartFile> imageFiles;
    private String deleteImage;
}
