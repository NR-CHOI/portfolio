package com.hotel.reservation.domain.roomInfoImage;

import com.hotel.reservation.domain.roomInfo.RoomInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomInfoImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="roomInfoImage_id")
    private Long imageId;

    //uuid로 저장된 파일명
    private String storeFileName;
    //S3 url
    private String storeS3Url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="room_info_id")
    private RoomInfo roomInfo;

    public RoomInfoImage(String storeFileName, String url){
        this.storeFileName = storeFileName;
        this.storeS3Url = url;
    }
    public void addRoomInfo(RoomInfo roomInfo){
        this.roomInfo = roomInfo;
    }
}
