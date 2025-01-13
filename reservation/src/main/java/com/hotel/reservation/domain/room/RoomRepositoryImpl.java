package com.hotel.reservation.domain.room;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static com.hotel.reservation.domain.room.QRoom.room;
import static com.hotel.reservation.domain.roomInfo.QRoomInfo.roomInfo;

public class RoomRepositoryImpl implements RoomRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public RoomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    //예약가능 객실검색(인원수, 체크인, 체크아웃 조건으로 검색)
    @Override
    public Slice<RoomSearchResult> search(RoomSearchDto cond, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        List<RoomSearchResult> content = queryFactory
                .select(new QRoomSearchResult(
                        roomInfo.roomInfoId,
                        roomInfo.roomType,
                        room.price.sum(),
                        room.quantity.min(),
                        room.roomId.count()))
                .from(room)
                .leftJoin(room.roomInfo, roomInfo)
                .where(room.date.goe(cond.getCheckInDate()),
                        room.date.lt(cond.getCheckOutDate()),
                        room.status.isTrue(),
                        roomInfo.maxNum.multiply(cond.getQuantity()).goe(cond.getPeople()),
                        room.quantity.goe(cond.getQuantity()))
                .groupBy(roomInfo.roomInfoId)
                .having(room.roomId.count().goe(cond.getStayDate()))
                .orderBy(room.price.asc())
                .offset(pageable.getOffset())
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageSize) {
            content.remove(pageSize);
            hasNext = true;
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }

    //년, 월, 객실타입에 일치하는 객실 검색
    @Override
    public List<CalendarDto> findByYearAndMonthAndRoomType(LocalDateTime date, Long id, Long roomInfoId) {
        StringExpression searchDate = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, '%Y-%m')", date);
        StringExpression roomDate = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, '%Y-%m')", room.date);
        return queryFactory
                .select(new QCalendarDto(room.roomId, room.date, roomInfo.roomType, room.price, room.quantity, room.status))
                .from(room)
                .leftJoin(room.roomInfo, roomInfo)
                .where(roomDate.eq(searchDate), roomInfo.member.id.eq(id), roomInfo.roomInfoId.eq(roomInfoId))
                .fetch();
    }
}
