package com.hotel.reservation.domain.roomInfo;

import com.hotel.reservation.domain.member.Member;
import com.hotel.reservation.domain.roomInfoImage.RoomInfoImage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Entity
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class RoomInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="room_info_id")
    private Long roomInfoId;
    private Integer roomNo;
    private String roomType;
    private int maxNum;

    @OneToMany(mappedBy="roomInfo", cascade= CascadeType.ALL, orphanRemoval=true)
    private List<RoomInfoImage> roomInfoImages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    private void setImageFiles(RoomInfoImage image) {
        this.roomInfoImages.add(image);
        image.addRoomInfo(this);
    }

    private RoomInfo(Integer roomNo, String roomType, int maxNum, Member member) {
        this.roomNo = roomNo;
        this.roomType = roomType;
        this.maxNum = maxNum;
        this.member = member;
    }

    public static RoomInfo createRoomInfo(Integer roomNo, String roomType, int maxNum, Member member, List<RoomInfoImage> imageFiles) {
        RoomInfo roomInfo = new RoomInfo(roomNo, roomType, maxNum, member);
        imageFiles.forEach(imageFile->roomInfo.setImageFiles(imageFile));
        return roomInfo;
    }

    public static RoomInfo createRoomInfoWithNoFile(Integer roomNo, String roomType, int maxNum, Member member) {
        RoomInfo roomInfo = new RoomInfo(roomNo, roomType, maxNum, member);
        return roomInfo;
    }

    public void update(Integer roomNo, String roomType, int maxNum) {
        this.roomNo = roomNo;
        this.roomType = roomType;
        this.maxNum = maxNum;
    }
    public void updateImages(List<RoomInfoImage> imageFiles) {
        imageFiles.forEach(imageFile-> this.setImageFiles(imageFile));
    }
}
