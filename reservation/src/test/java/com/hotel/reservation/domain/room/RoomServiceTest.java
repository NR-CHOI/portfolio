package com.hotel.reservation.domain.room;

import com.hotel.reservation.domain.member.Member;
import com.hotel.reservation.domain.roomInfo.RoomInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
@Slf4j
class RoomServiceTest {
    @Autowired EntityManager em;
    @Autowired RoomService roomService;

    @Test
    void search() {

        Member member = Member.createMember("차은우", "cha@gmail.com", "Rain1234!@", "01000000000");
        em.persist(member);

        RoomInfo tripleRoom = RoomInfo.createRoomInfo(6, "triple", 3, member, null);
        em.persist(tripleRoom);

        //객실재고 추가
        LocalDateTime startDate = LocalDateTime.of(2024, 8, 2, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 8, 3, 0, 0);

        List<LocalDateTime> dates = Stream.iterate(startDate, localDateTime -> localDateTime.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startDate, endDate.plusDays(1)))
                .collect(Collectors.toList());

        dates.forEach(date -> {
            RoomAddDto roomAddDto = new RoomAddDto(date, 50000, 2, true, tripleRoom);
            Room room = Room.createRoom(roomAddDto);
            em.persist(room);
        });
// 재고<숙박일수 일 경우, stayDate<=재고 수
// 재고=숙박일수 일 경우,  재고 전부 다 나오기 때문에 조건추가

        //검색
        LocalDateTime checkInDate = LocalDateTime.of(2024, 8, 3, 0, 0);
        LocalDateTime checkOutDate = LocalDateTime.of(2024, 8, 4, 0, 0);
        long stayDate = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

        RoomSearchDto dto = new RoomSearchDto(1, 3, checkInDate, checkOutDate, stayDate);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "roomId"));
        Slice<RoomSearchResult> roomList = roomService.search(dto, pageRequest);

        for(RoomSearchResult room : roomList){
//            log.info("roomId={}, roomQuantity={}, roomPrice={}", room.getRoomId(), room.getQuantity(), room.getPrice());
        }

        assertThat(roomList.getNumberOfElements()).isEqualTo(1);

//        List<Long> result = roomService.search(dto);
//        assertThat(result).isEqualTo(2L);

    }

    @Test
    void search2() {

        Member member = Member.createMember("차은우", "cha@gmail.com", "Rain1234!@", "01000000000");
        em.persist(member);

        RoomInfo tripleRoom = RoomInfo.createRoomInfo(6, "triple", 3, member, null);
        em.persist(tripleRoom);

        //객실재고 추가
        LocalDateTime startDate = LocalDateTime.of(2024, 8, 2, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 8, 4, 0, 0);

        List<LocalDateTime> dates = Stream.iterate(startDate, localDateTime -> localDateTime.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startDate, endDate.plusDays(1)))
                .collect(Collectors.toList());

        dates.forEach(date -> {
                    RoomAddDto roomAddDto = new RoomAddDto(date, 50000, 2, true, tripleRoom);
                    Room room = Room.createRoom(roomAddDto);
                    em.persist(room);
                });

        //검색
        LocalDateTime checkInDate = LocalDateTime.of(2024, 8, 3, 0, 0);
        LocalDateTime checkOutDate = LocalDateTime.of(2024, 8, 4, 0, 0);
        long stayDate = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

        RoomSearchDto dto = new RoomSearchDto(1, 3, checkInDate, checkOutDate, stayDate);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "roomId"));
        Slice<RoomSearchResult> roomList = roomService.search(dto, pageRequest);

        for(RoomSearchResult room : roomList){
//            log.info("roomId={}, roomQuantity={}, roomPrice={}", room.getRoomId(), room.getQuantity(), room.getPrice());
        }

        assertThat(roomList.getNumberOfElements()).isEqualTo(0);

//        List<Long> result = roomService.search(dto);
//        assertThat(result).isEqualTo(2L);

    }

    @Test
    void search3() {

        Member member = Member.createMember("차은우", "cha@gmail.com", "Rain1234!@", "01000000000");
        em.persist(member);

        RoomInfo tripleRoom = RoomInfo.createRoomInfo(6, "triple", 3, member, null);
        em.persist(tripleRoom);

        //객실재고 추가
        LocalDateTime startDate = LocalDateTime.of(2024, 8, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 8, 3, 0, 0);

        List<LocalDateTime> dates = Stream.iterate(startDate, localDateTime -> localDateTime.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startDate, endDate.plusDays(1)))
                .collect(Collectors.toList());

        dates.forEach(date -> {
            RoomAddDto roomAddDto = new RoomAddDto(date, 50000, 1, true, tripleRoom);
            Room room = Room.createRoom(roomAddDto);
            em.persist(room);
        });


        //검색
        LocalDateTime checkInDate = LocalDateTime.of(2024, 8, 3, 0, 0);
        LocalDateTime checkOutDate = LocalDateTime.of(2024, 8, 4, 0, 0);
        int quantity = 1;
        int people = 3;

        long stayDate = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

        RoomSearchDto dto = new RoomSearchDto(1, 3, checkInDate, checkOutDate, stayDate);

//        String sqlString="select count(r) from Room r where r.date >= :checkInDate and r.date < :checkOutDate and r.status = true";
//        String sqlString="select r from Room r where r.date >= :checkInDate and r.date < :checkOutDate and r.status = true";
        String sqlString="select r from Room r left join r.roomInfo ri" +
                " where r.date >= :checkInDate and r.date < :checkOutDate and r.status = true" +
                " and ri.maxNum * :quantity >= :people and r.quantity >= :quantity";
        List<Room> result= em.createQuery(sqlString, Room.class)
                .setParameter("checkInDate", checkInDate)
                .setParameter("checkOutDate", checkOutDate)
                .setParameter("quantity", quantity)
                .setParameter("people", people)
                .getResultList();

        assertThat(result.size()).isEqualTo(1);

        for(Room room : result){
            log.info("roomId={}, roomQuantity={}, roomPrice={}", room.getRoomId(), room.getQuantity(), room.getPrice());
        }
    }
}