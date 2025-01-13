package com.hotel.reservation.domain.roomInfoImage;

import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hotel.reservation.domain.roomInfoImage.QRoomInfoImage.roomInfoImage;
import static java.util.stream.Collectors.toList;

public class RoomInfoImageRepositoryImpl implements RoomInfoImageRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    public RoomInfoImageRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }
    //객실이미지 파일이름 찾기
    @Override
    public Map<Long, List<String>> findByRoomInfoId(Set<Long> roomInfoIds) {
        List<RoomInfoImageDto> result = queryFactory
                .select(new QRoomInfoImageDto(roomInfoImage.roomInfo.roomInfoId, roomInfoImage.storeS3Url))
                .from(roomInfoImage)
                .where(roomInfoImage.roomInfo.roomInfoId.in(roomInfoIds))
                .fetch();

        return result.stream().collect(Collectors.groupingBy(RoomInfoImageDto::getRoomInfoId,
                Collectors.mapping(RoomInfoImageDto::getStoreFileName, toList())));
    }
}
