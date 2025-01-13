package com.hotel.reservation.domain.room;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomRepositoryCustom {

    //예약가능 객실검색(고객용)
    Slice<RoomSearchResult> search(RoomSearchDto cond, Pageable pageable);

    //년, 월, 객실타입에 일치하는 객실 검색
    List<CalendarDto> findByYearAndMonthAndRoomType(LocalDateTime date, Long id, Long roomInfoId);

}
