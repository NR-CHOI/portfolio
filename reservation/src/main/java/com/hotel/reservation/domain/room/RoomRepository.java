package com.hotel.reservation.domain.room;

import com.hotel.reservation.domain.roomInfo.RoomInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long>, RoomRepositoryCustom {
    //등록자별 객실 전체검색(목록형, 관리자용, 페이징O)
    @Query(value = "select new com.hotel.reservation.domain.room.RoomByMemberDto(r.roomId, ri.roomType, r.date, r.price, r.quantity, r.status)" +
            "from Room r join r.roomInfo ri where ri.member.id = :id",
            countQuery = "select count(r.roomId) from Room r")
    Page<RoomByMemberDto> findByMemberId(@Param("id") Long id, Pageable pageable);

    //객실재고 등록한 아이디 검색(LoginIdCheckInterceptor)
    @Query("select ri.member.id from Room r join r.roomInfo ri where r.roomId= :id")
    Long findRegisterId(@Param("id") Long id);

    //해당 날짜와 객실유형에 일치하는 객실재고가 있는지 체크하기
    @Query("select r.roomId from Room r where r.date= :date and r.roomInfo.roomInfoId= :roomInfoId")
    Long findByDateAndRoomInfo(@Param("date") LocalDateTime date, @Param("roomInfoId") Long roomInfoId);

    //해당 객실타입과 일치하는 객실재고 전부 검색.
    List<Room> findByRoomInfo(RoomInfo roomInfo);

    //해당조건 && roomInfoId가 일치하는 객실재고 가져오기
    @Query("select new com.hotel.reservation.domain.room.RoomSearchRoomsResult(ri.roomInfoId, r.roomId, ri.roomType, r.price, r.quantity, r.date) from Room r left join r.roomInfo ri where" +
            " r.date >= :checkInDate and r.date < :checkOutDate and r.status = true" +
            " and ri.maxNum * :quantity >= :people and r.quantity >= :quantity" +
            " and ri.roomInfoId in :roomInfoId")
    List<RoomSearchRoomsResult> searchRooms(
//            @Param("stayDate") Long stayDate,
            @Param("checkInDate") LocalDateTime checkInDate,
            @Param("checkOutDate") LocalDateTime checkOutDate,
            @Param("quantity") int quantity, @Param("people") int people, @Param("roomInfoId") List<Long> roomInfoId);

}
