package com.hotel.reservation.domain.booking;

import com.hotel.reservation.domain.bookedRoom.BookedRoom;
import com.hotel.reservation.domain.bookedRoom.BookedRoomDto;
import com.hotel.reservation.domain.member.Member;
import com.hotel.reservation.domain.member.MemberRepository;
import com.hotel.reservation.domain.room.Room;
import com.hotel.reservation.domain.room.RoomRepository;
import com.hotel.reservation.domain.roomInfoImage.RoomInfoImageRepository;
import com.hotel.reservation.web.booking.form.*;
import com.hotel.reservation.domain.room.RoomSearchRoomsResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final RoomInfoImageRepository roomInfoImageRepository;
    private final MemberRepository memberRepository;

    //예약
    @Transactional
    public void Booking(List<BookingForm> bookingFormList) {
        //고객이 count=0으로 설정한 객실뺴고 예약 진행하기
        List<Long> roomInfoIds = bookingFormList.stream().filter(bookingForm -> bookingForm.getCount() > 0).map(BookingForm::getRoomInfoId).collect(Collectors.toList());
        roomInfoIds.forEach(roomInfoId->log.info("roomInfoId={}", roomInfoId));

        //해당조건 && roomInfoId가 일치하는 room 재고 정보 가져오기
        BookingForm firstList = bookingFormList.get(0);
        List<RoomSearchRoomsResult> findRooms = roomRepository.searchRooms(firstList.getCheckInDate().atStartOfDay(), firstList.getCheckOutDate().atStartOfDay(), firstList.getQuantity(), firstList.getPeople(), roomInfoIds);

        //BookedRoom 객체 생성
        List<BookedRoom> bookedRooms = new ArrayList<>();
        for (RoomSearchRoomsResult findRoom : findRooms) {
            for (BookingForm bookingForm : bookingFormList) {
                if(findRoom.getRoomInfoId() == bookingForm.getRoomInfoId()) {
                    BookedRoomDto BookedRoomDto = new BookedRoomDto(findRoom.getPrice(), bookingForm.getCount());
                    Room room = roomRepository.findById(findRoom.getRoomId()).orElseThrow(()-> new NoSuchElementException("해당하는 객실이 없습니다."));
                    BookedRoom bookedRoom = BookedRoom.createBookedRoom(room, BookedRoomDto);
                    bookedRooms.add(bookedRoom);
                }
            }
        }
        LocalDateTime checkInDate = LocalDateTime.of(firstList.getCheckInDate(), LocalTime.of(0, 0));
        LocalDateTime checkOutDate = LocalDateTime.of(firstList.getCheckOutDate(), LocalTime.of(0, 0));
        BookingDateDto BookingDateDto = new BookingDateDto(checkInDate, checkOutDate);
        int people = firstList.getPeople();
        //예약 객체 생성 후 저장
        Member findMember = memberRepository.findById(firstList.getMemberId()).orElseThrow(()-> new NoSuchElementException("해당하는 회원이 없습니다."));
        Booking booking = Booking.createBooking(BookingDateDto, findMember, bookedRooms, people);
        bookingRepository.save(booking);
    }

    //예약취소
    @Transactional
    public void cancel(Long bookingId) {
        Booking findBooking = bookingRepository.findById(bookingId).orElseThrow(NoSuchElementException::new);
        findBooking.cancel();
    }

    //예약 검색(괸리자용, 페이징O)
    public Page<BookingListResponse> findAllBookings(BookingSearchCondition condition, Pageable pageable) {
        //관리자가 등록한 객실의 모든 예약내역
        Page<BookingListResponse> result = bookingRepository.findAllBookings(condition, pageable);
        //예약번호로 예약된 객실들 조회
        Map<Long, List<BookedRoomResponse>> bookedRoomMap = bookingRepository.findBookedRoomMap(bookingIds(result));
        result.forEach(b ->
                b.setBookedRooms(bookedRoomMap.get(b.getBookingId()))
        );
        return result;
    }
    //예약번호 추출
    private List<Long> bookingIds(Page<BookingListResponse> result) {
        return result.stream().map(b -> b.getBookingId()).collect(Collectors.toList());
    }

    //예약정보 상세보기(관리자용)
    public BookingListResponse findBooking(Long bookingId) {
        //예약번호로 예약정보 조회
        BookingListResponse findBooking = bookingRepository.findBooking(bookingId);
        List<BookedRoomResponse> findBookedRooms = bookingRepository.findBookedRoom(bookingId);
        findBooking.setBookedRooms(findBookedRooms);
        return findBooking;
    }

    //내 예약 리스트(페이징O)
    public Page<BookingListResponseWithImage> findBookingByMemberId(MyReservationCond cond, Pageable pageable){
        //내 예약 리스트(1:1)
        Page<BookingListResponseWithImage> result = bookingRepository.findBookingByMemberId(cond, pageable);
        //예약번호에 해당하는 예약된 객실 찾아오기(1:N)
        List<BookedRoomResponseWithImage> bookedRoom = bookingRepository.findBookedRoomByMemberId(bookingIdsForMember(result));
        //객실이미지 파일이름 찾아오기(1:N)
        Map<Long, List<String>> images = roomInfoImageRepository.findByRoomInfoId(roomInfoIds(bookedRoom));
        //객실에 이미지 파일이름 add
        bookedRoom.forEach(r-> r.setStoreS3Urls(images.get(r.getRoomInfoId())));
        Map<Long, List<BookedRoomResponseWithImage>> collect = bookedRoom.stream().collect(Collectors.groupingBy(BookedRoomResponseWithImage::getBookingId));
        //내 예약에 예약된 객실 값 넣기
        result.forEach(b->b.setBookedRooms(collect.get(b.getBookingId())));
        return result;
    }
    //예약번호 추출
    private List<Long> bookingIdsForMember(Page<BookingListResponseWithImage> result) {
        return result.stream().map(b -> b.getBookingId()).collect(Collectors.toList());
    }
    //객실유형 id 추출
    private Set<Long> roomInfoIds(List<BookedRoomResponseWithImage> bookedRoom){
        return bookedRoom.stream().map(r -> r.getRoomInfoId()).collect(Collectors.toSet());
    }
    //내 예약 상세보기
    public BookingListResponseWithImage findBookingWithImage(Long bookingId){
        //내예약(1:1)
        BookingListResponseWithImage booking = bookingRepository.findBookingWithImage(bookingId);
        //예약번호에 해당하는 예약된 객실 찾아오기(1:N)
        List<BookedRoomResponseWithImage> bookedRoom = bookingRepository.findBookedRoomWithImage(bookingId);
        //객실이미지 파일이름 찾아오기(1:N)
        Map<Long, List<String>> images = roomInfoImageRepository.findByRoomInfoId(roomInfoIds(bookedRoom));
        //객실에 이미지 파일이름 add
        bookedRoom.forEach(r ->
                r.setStoreS3Urls(images.get(r.getRoomInfoId())));
        booking.setBookedRooms(bookedRoom);
        return booking;
    }
}