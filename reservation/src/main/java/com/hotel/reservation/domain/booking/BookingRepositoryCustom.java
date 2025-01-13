package com.hotel.reservation.domain.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface BookingRepositoryCustom {
    //관리자용
    //관리자가 등록한 객실의 모든 예약내역(페이징O)
    Page<BookingListResponse> findAllBookings(BookingSearchCondition condition, Pageable pageable);
    //예약번호로 예약된 객실들 조회
    Map<Long, List<BookedRoomResponse>> findBookedRoomMap(List<Long> bookingIds);

    //예약번호로 예약정보 조회
    BookingListResponse findBooking(Long bookingId);
    //예약번호로 예약된 객실 조회
    List<BookedRoomResponse> findBookedRoom(Long bookingId);

    //일반회원용
    //내 예약 조회(페이징O)
    Page<BookingListResponseWithImage> findBookingByMemberId(MyReservationCond cond, Pageable pageable);
    //예약번호로 예약된 객실 조회
    List<BookedRoomResponseWithImage> findBookedRoomByMemberId(List<Long> bookingIds);

    //내 예약 상세보기
    BookingListResponseWithImage findBookingWithImage(Long bookingId);
    //예약번호로 예약된 객실 조회
    List<BookedRoomResponseWithImage> findBookedRoomWithImage(Long bookingId);
}
