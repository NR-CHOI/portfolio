package com.hotel.reservation.domain.booking;

import com.hotel.reservation.domain.bookedRoom.BookedRoom;
import com.hotel.reservation.domain.member.Member;
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
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<BookedRoom> bookedRooms = new ArrayList<>();
    private LocalDateTime bookingDate;
    private LocalDateTime arrivalDate;
    private LocalDateTime departureDate;
    private int people;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    //연관관계 메서드
    public void setMember(Member member) {
        this.member = member;
        member.getBookings().add(this);
    }
    public void addBookedRoom(BookedRoom bookedRoom) {
        bookedRooms.add(bookedRoom);
        bookedRoom.addBooking(this);
    }
    private Booking(LocalDateTime arrivalDate, LocalDateTime departureDate, int people) {
        this.bookingDate = LocalDateTime.now();
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.status = BookingStatus.CONFIRMED;
        this.people = people;
    }
    //생성 메서드
    public static Booking createBooking(BookingDateDto dto, Member member, List<BookedRoom> bookedRooms, int people) {
        Booking booking = new Booking(dto.getArrivalDate(), dto.getDepartureDate(), people);
        booking.setMember(member);
        for (BookedRoom bookedRoom : bookedRooms) {
            booking.addBookedRoom(bookedRoom);
        }
        return booking;
    }
    //비즈니스 로직(예약 취소)
    public void cancel() {
        this.status = BookingStatus.CANCELLED;
        for (BookedRoom bookedRoom : bookedRooms) {
            bookedRoom.cancel();
        }
    }
}
