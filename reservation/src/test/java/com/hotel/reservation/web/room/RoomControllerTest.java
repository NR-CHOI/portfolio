package com.hotel.reservation.web.room;

import com.hotel.reservation.domain.member.Member;
import com.hotel.reservation.domain.member.MemberService;
import com.hotel.reservation.domain.roomInfo.RoomInfo;
import com.hotel.reservation.domain.room.RoomRepository;
import com.hotel.reservation.domain.roomInfo.RoomInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback(false)
class RoomControllerTest {

    @Autowired
    RoomRepository roomRepository;
    @Autowired
    RoomInfoService roomInfoService;

    @Autowired MemberService memberService;

    @Test
    public void bulkUpdate() {
        Member member = Member.createMember("차은우", "hyun@gmail.com", "Rain1234!@", "010-0000-0000");
        memberService.save(member);

        RoomInfo roomInfo = RoomInfo.createRoomInfo(101, "single", 1, member, null);
//        roomInfoService.addRoom(roomInfo);


//        RoomsAddDto dto = new RoomsAddDto(LocalDateTime.of(2024, 2, 27, 0, 0),
//                LocalDateTime.of(2024, 3, 27, 0, 0),
//                30000,
//                2,
//                true,
//                roomInfo
//                );
//
//        roomRepository.addRooms(dto);
    }
}