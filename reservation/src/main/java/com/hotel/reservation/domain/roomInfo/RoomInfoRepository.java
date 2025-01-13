package com.hotel.reservation.domain.roomInfo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomInfoRepository extends JpaRepository<RoomInfo, Long> {

    //등록자별 객실 전체검색
    @Query("select new com.hotel.reservation.domain.roomInfo.RoomInfoDto(ri.roomInfoId, ri.roomType) from RoomInfo ri where ri.member.id = :id ")
    List<RoomInfoDto> findByMemberId(@Param("id") Long id);

    //관리자가 등록한 모든 객실유형(페이징O)
    @Query(value= "select ri from RoomInfo ri where ri.member.id = :id",
            countQuery = "select count(ri.roomInfoId) from RoomInfo ri")
    Page<RoomInfo> findAllByMemberId(@Param("id") Long id, Pageable pageable);

}
