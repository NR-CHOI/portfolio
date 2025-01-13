package com.hotel.reservation.domain.booking;

import com.hotel.reservation.domain.booking.BookedRoomResponse;
import com.hotel.reservation.domain.booking.BookingStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BookingListResponse {
    private String name;
    private String phoneNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime arrivalDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime departureDate;

    private List<BookedRoomResponse> bookedRooms;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime bookingDate;

    private BookingStatus status;
    private Long bookingId;
    private int people;

    public BookingListResponse() {
    }

    @QueryProjection
    public BookingListResponse(String name, String phoneNumber, LocalDateTime arrivalDate, LocalDateTime departureDate,
                               LocalDateTime bookingDate, BookingStatus status, Long bookingId, int people) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.bookingDate = bookingDate;
        this.status = status;
        this.bookingId = bookingId;
        this.people = people;
    }
}
