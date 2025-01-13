package com.hotel.reservation.domain.booking;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@Rollback(value = false)
class BookingRepositoryImplTest {
    /*

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    BookingService bookingService;

    //예약성공
    @Test
    void booking_success() {
        //Given
        Member manager = createManager();
        Member user = createUser();
        Room room = createRoom(201, "twin", 2, 40000, 11, true, manager);
        //When
        Long bookingId = bookingService.Booking(room,
                new BookedRoomDto(LocalDateTime.of(2024, 2, 27, 0, 0), 35000, 1),
                new BookingDateDto(LocalDateTime.of(2024, 2, 27, 0, 0),
                        LocalDateTime.of(2024, 2, 28, 0, 0)),
                user);
        //Then
        Booking findBooking = bookingService.findById(bookingId);

        Assertions.assertThat(room.getQuantity()).isEqualTo(10);
        Assertions.assertThat(findBooking.getStatus()).isEqualTo(CONFIRMED);
        Assertions.assertThat(findBooking.getMember().getName()).isEqualTo("teo");
    }

    //객실 수량 부족
    @Rollback
    @Test
    void booking_fail() {
        //Given
        Member manager = createManager();
        Member user = createUser();
        Room room = createRoom(201, "twin", 2, 40000, 1, true, manager);
        //When
        //Then
        assertThrows(NotEnoughStockException.class,
                () -> {
                    bookingService.Booking(room,
                            new BookedRoomDto(LocalDateTime.of(2024, 2, 27, 0, 0), 35000, 2),
                            new BookingDateDto(LocalDateTime.of(2024, 2, 27, 0, 0),
                                    LocalDateTime.of(2024, 2, 28, 0, 0)),
                            user);
                });
    }

    //객실 수량 부족2
    @Rollback
    @Test
    void booking_fail_second() {
        try {
            //Given
            Member manager = createManager();
            Member user = createUser();
            Room room = createRoom(201, "twin", 2, 40000, 1, true, manager);
            //When
            bookingService.Booking(room,
                    new BookedRoomDto(LocalDateTime.of(2024, 2, 27, 0, 0), 35000, 2),
                    new BookingDateDto(LocalDateTime.of(2024, 2, 27, 0, 0),
                            LocalDateTime.of(2024, 2, 28, 0, 0)),
                    user);
        } catch (NotEnoughStockException e) {
            assertEquals(e.getMessage(), "예약가능한 객실의 수량이 부족합니다.");
        }
    }

    //예약취소
    @Test
    void cancel() {
        //Given
        Member manager = createManager();
        Member user = createUser();
        Room room = createRoom(201, "twin", 2, 40000, 11, true, manager);
        //When
        Long bookingId = bookingService.Booking(room,
                new BookedRoomDto(LocalDateTime.of(2024, 2, 27, 0, 0), 35000, 1),
                new BookingDateDto(LocalDateTime.of(2024, 2, 27, 0, 0),
                        LocalDateTime.of(2024, 2, 28, 0, 0)),
                user);
        //Then
        bookingService.cancel(bookingId);
        Booking findBooking = bookingService.findById(bookingId);
        Assertions.assertThat(findBooking.getStatus()).isEqualTo(CANCELLED);
        Assertions.assertThat(room.getQuantity()).isEqualTo(11);
    }

    //예약검색(고객용)
    //검색기준: memberId
    @Test
    void searchForGuest() {
        //Given
        Member manager = createManager();
        Member user = createUser();
        Room twinRoom = createRoom(LocalDateTime.of(2024, 2, 27, 0, 0), 50000, 2, true, 1);
        Room doubleRoom = createRoom(301, "double", 2, 40000, 11, true, manager);
        //When
        bookingService.Booking(twinRoom,
                new BookedRoomDto(LocalDateTime.of(2024, 2, 27, 0, 0), 35000, 1),
                new BookingDateDto(LocalDateTime.of(2024, 2, 27, 0, 0),
                        LocalDateTime.of(2024, 2, 28, 0, 0)),
                user);
        bookingService.Booking(doubleRoom,
                new BookedRoomDto(LocalDateTime.of(2024, 2, 26, 0, 0), 35000, 1),
                new BookingDateDto(LocalDateTime.of(2024, 2, 26, 0, 0),
                        LocalDateTime.of(2024, 2, 27, 0, 0)),
                user);
        //Then
        List<BookingSearchDto> bookingList = bookingService.findByMemberId(user.getId());
//        List<Booking> bookingList = bookingService.findByMemberId(user.getId());
        for (BookingSearchDto booking : bookingList) {
            System.out.println(booking);
        }
        Assertions.assertThat(bookingList).extracting("roomType").containsExactly("twin", "double");
    }

    //예약검색(관리자용)
    //검색기준: 예약자명, 객실타입, 체크인날짜, 체크아웃날짜, 예약날짜
    @Test
    void searchForManager() {
        Member manager = createManager();
        Member user = createUser();
        Room familyRoom = createRoom(301, "family room", 4, 120000, 10, true, manager);
        Room twinRoom = createRoom(201, "twin room", 2, 50000, 5, true, manager);

        bookingService.Booking(familyRoom,
                new BookedRoomDto(LocalDateTime.of(2024, 2, 27, 0, 0), 120000, 1),
                new BookingDateDto(LocalDateTime.of(2024, 2, 27, 0, 0),
                        LocalDateTime.of(2024, 2, 28, 0, 0)),
                user);

        bookingService.Booking(twinRoom,
                new BookedRoomDto(LocalDateTime.of(2024, 2, 26, 0, 0), 50000, 2),
                new BookingDateDto(LocalDateTime.of(2024, 2, 26, 0, 0),
                        LocalDateTime.of(2024, 2, 28, 0, 0)),
                user);

        BookingSearchCond cond = new BookingSearchCond();
        cond.setDepartureDate(LocalDateTime.of(2024, 2, 28, 0, 0));
        cond.setRoomType("family room");

        List<BookingSearchDto> result = bookingRepository.search(cond);
        for (BookingSearchDto dto : result) {
            System.out.println(dto.toString());
            Assertions.assertThat(dto.getCount()).isEqualTo(1);
        }
        Assertions.assertThat(result).extracting("name").containsExactly("teo");
        Assertions.assertThat(result).extracting("status").containsExactly(CONFIRMED);
    }


    private Member createManager() {
        //객실관리자 생성
        Member member = new Member("manager", "siri@gmail.com", "Rain1234!@", "01012341234");
        return memberRepository.save(member);
    }

    private Member createUser() {
        //예약자 생성
        Member member2 = new Member("teo", "siri@gmail.com", "Rain1234!@", "01012341234");
        return memberRepository.save(member2);
    }

    private Room createRoom(LocalDateTime date, Integer price, Integer quantity, Boolean status, RoomInfo roomInfo) {
        //객실생성
        RoomDataDto dto = new RoomDataDto(date, price, quantity, status, roomInfo);
        Room room = Room.createRoom(dto);
        return roomRepository.save(room);
    }
*/
}