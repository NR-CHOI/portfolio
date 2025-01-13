package com.hotel.reservation.domain.booking;

import com.hotel.reservation.domain.booking.BookedRoomResponseWithImage;
import com.hotel.reservation.domain.booking.BookingStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BookingListResponseWithImage {
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime arrivalDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime departureDate;

    private List<BookedRoomResponseWithImage> bookedRooms;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime bookingDate;

    private BookingStatus status;
    private Long bookingId;
    private int people;

    public BookingListResponseWithImage() {
    }

    @QueryProjection
    public BookingListResponseWithImage(String name, LocalDateTime arrivalDate, LocalDateTime departureDate,
                               LocalDateTime bookingDate, BookingStatus status, Long bookingId, int people) {
        this.name = name;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.bookingDate = bookingDate;
        this.status = status;
        this.bookingId = bookingId;
        this.people = people;
    }
}
