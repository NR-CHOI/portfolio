package com.hotel.reservation.domain.roomInfoImage;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RoomInfoImageRepositoryCustom {
    //객실이미지 파일이름 찾기
    Map<Long, List<String>> findByRoomInfoId(Set<Long> roomInfoIds);
}
