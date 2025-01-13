package com.hotel.reservation.domain.room;

import com.hotel.reservation.domain.member.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@Rollback(value = false)
class RoomRepositoryCustomTest {



    @Autowired
    RoomRepository roomRepository;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void booking_success() {


//        RoomSearchForm cond = new RoomSearchForm(LocalDateTime.of(2024, 2, 27, 0, 0),
//                LocalDateTime.of(2024, 2, 28, 0, 0),
//                2);
        LocalDateTime checkIn = LocalDateTime.of(2024, 8, 02, 0, 0);
        LocalDateTime checkOut = LocalDateTime.of(2024, 8, 04, 0, 0);
        List<Long> roomInfoIds = new ArrayList<>();
        roomInfoIds.add(Long.parseLong("1"));
        roomInfoIds.add(Long.parseLong("2"));

        List<RoomSearchRoomsResult> results = roomRepository.searchRooms(checkIn, checkOut, 2, 2, roomInfoIds);

        Assertions.assertThat(results).isEqualTo(4);
//        Assertions.assertThat(results).extracting("roomType").containsExactly("twin");
//        Assertions.assertThat(result).extracting("price").containsExactly(40000);
    }






 /*

    private Member createManager() {
        //객실관리자 생성
        Member member = new Member("manager", "siri@gmail.com", "Rain1234!@", "01012341234");
        return memberRepository.save(member);
    }

    private Member createUser() {
        //예약자 생성
        Member member2 = new Member("member", "siri@gmail.com", "Rain1234!@", "01012341234");
        return memberRepository.save(member2);
    }

    private Room createRoom(Integer roomNo, String roomType, Integer maxNum, Integer price, Integer quantity, Boolean status, Member manager) {
        //객실생성
        RoomDataDto dto = new RoomDataDto(roomNo, roomType, maxNum, price, quantity, status);
        Room room = Room.createRoom(dto, manager);
        return roomRepository.save(room);
    }

*/
}