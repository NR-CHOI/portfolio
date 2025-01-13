package com.hotel.reservation.domain.bookedRoom;

import com.hotel.reservation.domain.booking.Booking;
import com.hotel.reservation.domain.room.Room;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "booked_room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookedRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booked_room_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;
    private int bookingPrice;
    private int count;

    //연관관계 편의 메서드
    public void setRoom(Room room){
        this.room = room;
        room.getBookedRooms().add(this);
    }
    //@setter 사용안하기위해 만든 메서드
    public void addBooking(Booking booking){
        this.booking = booking;
    }
    private BookedRoom(int bookingPrice, int count){
        this.bookingPrice = bookingPrice;
        this.count = count;
    }
    //정적 팩토리 매서드
    public static BookedRoom createBookedRoom(Room room, BookedRoomDto dto) {
        BookedRoom bookedRoom = new BookedRoom(dto.getBookingPrice(), dto.getCount());
        bookedRoom.setRoom(room);
        room.removeStock(dto.getCount());
        return bookedRoom;
    }
    //비즈니스 로직(예약 취소시 객실재고 수량 원복)
    public void cancel(){
        room.addStock(count);
    }
}
