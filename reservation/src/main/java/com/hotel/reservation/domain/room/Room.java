package com.hotel.reservation.domain.room;

import com.hotel.reservation.domain.bookedRoom.BookedRoom;
import com.hotel.reservation.domain.roomInfo.RoomInfo;
import com.hotel.reservation.exception.NotEnoughStockException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="room_id")
    private Long roomId;
    private LocalDateTime date;
    private int price;
    private int quantity;
    private Boolean status;

    @OneToMany(mappedBy = "room")
    private List<BookedRoom> bookedRooms = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="room_info_id")
    private RoomInfo roomInfo;

    private Room(LocalDateTime date, int price, int quantity, Boolean status, RoomInfo roomInfo) {
        this.date = date;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
        this.roomInfo = roomInfo;
    }

    //정적 팩토리 메서드
    public static Room createRoom(RoomAddDto dto){
        Room room = new Room(dto.getDate(), dto.getPrice(), dto.getQuantity(), dto.getStatus(), dto.getRoomInfo());
        return room;
    }

    public void updateRoom(RoomUpdateDto dto){
        this.date = dto.getDate();
        this.price = dto.getPrice();
        this.quantity = dto.getQuantity();
        this.status = dto.getStatus();
        this.roomInfo = dto.getRoomInfo();
    }

    //비즈니스 로직(예약취소시 객실재고 수량 원복)
    public void addStock(int quantity){
        this.quantity += quantity;
    }

    //예약시 객실 재고 수량 줄이기
    public void removeStock(int quantity){
        int restStock = this.quantity - quantity;
        if(restStock<0){
            throw new NotEnoughStockException("예약가능한 객실의 수량이 부족합니다.");
        }
        this.quantity = restStock;
    }

}
