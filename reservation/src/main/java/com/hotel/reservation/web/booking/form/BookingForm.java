package com.hotel.reservation.web.booking.form;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class BookingForm {
    private Long memberId;
    private Long roomInfoId;
    private String roomType;
    private int price;
    private int count;  //예약자가 selectBox를 통해 선택한 객실 갯수

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;
    private int people;
    private int quantity;   //검색할때 입력한 객실 갯수
    private Long days;  //숙박일수
    private List<BookingForm> BookingFormList;
}
