package com.hotel.reservation.domain.booking;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hotel.reservation.domain.bookedRoom.QBookedRoom.bookedRoom;
import static com.hotel.reservation.domain.booking.QBooking.booking;
import static com.hotel.reservation.domain.member.QMember.member;
import static com.hotel.reservation.domain.room.QRoom.room;
import static com.hotel.reservation.domain.roomInfo.QRoomInfo.roomInfo;
import static org.thymeleaf.util.StringUtils.isEmpty;

public class BookingRepositoryImpl implements BookingRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    public BookingRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
    //관리자가 등록한 객실의 모든 예약내역
    @Override
    public Page<BookingListResponse> findAllBookings(BookingSearchCondition condition, Pageable pageable) {
        List<BookingListResponse> content = queryFactory
                .select(new QBookingListResponse(member.name, member.phoneNumber, booking.arrivalDate, booking.departureDate,
                        booking.bookingDate, booking.status, booking.bookingId, booking.people))
                .from(booking)
                .leftJoin(booking.member, member)
                .leftJoin(booking.bookedRooms, bookedRoom)
                .leftJoin(bookedRoom.room, room)
                .leftJoin(room.roomInfo, roomInfo)
                .where(roomInfo.member.id.eq(condition.getId()),
                        arrivalDateBetween(condition.getArrivalDateFrom(), condition.getArrivalDateUntil()),
                        departureDateBetween(condition.getDepartureDateFrom(), condition.getDepartureDateUntil()),
                        bookingDateBetween(condition.getBookingDateFrom(), condition.getBookingDateUntil()),
                        memberNameEq(condition.getName()),
                        bookingIdEq(condition.getBookingId()),
                        stayOverBetween(condition.getStayDateFrom(), condition.getStayDateUntil())
                )
                .groupBy(booking.bookingId)
                .orderBy(createOrderSpecifier(condition))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(booking.countDistinct())
                .from(booking)
                .leftJoin(booking.member, member)
                .leftJoin(booking.bookedRooms, bookedRoom)
                .leftJoin(bookedRoom.room, room)
                .leftJoin(room.roomInfo, roomInfo)
                .where(roomInfo.member.id.eq(condition.getId()),
                        arrivalDateBetween(condition.getArrivalDateFrom(), condition.getArrivalDateUntil()),
                        departureDateBetween(condition.getDepartureDateFrom(), condition.getDepartureDateUntil()),
                        bookingDateBetween(condition.getBookingDateFrom(), condition.getBookingDateUntil()),
                        memberNameEq(condition.getName()),
                        bookingIdEq(condition.getBookingId()),
                        stayOverBetween(condition.getStayDateFrom(), condition.getStayDateUntil()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    //예약번호로 예약된 객실들 조회
    @Override
    public Map<Long, List<BookedRoomResponse>> findBookedRoomMap(List<Long> bookingIds) {
        //1:N 관계인 bookedRooms 조회
        List<BookedRoomResponse> bookedRooms = queryFactory
                .select(new QBookedRoomResponse(bookedRoom.booking.bookingId, roomInfo.roomType, bookedRoom.bookingPrice, bookedRoom.count, room.date))
                .from(bookedRoom)
                .leftJoin(bookedRoom.room, room)
                .leftJoin(room.roomInfo, roomInfo)
                .where(bookedRoom.booking.bookingId.in(bookingIds))
                .fetch();
        return bookedRooms.stream()
                .collect(Collectors.groupingBy(BookedRoomResponse::getBookingId));
    }

    //예약번호로 예약정보 조회
    @Override
    public BookingListResponse findBooking(Long bookingId) {
        return queryFactory
                .select(new QBookingListResponse(member.name, member.phoneNumber, booking.arrivalDate, booking.departureDate,
                        booking.bookingDate, booking.status, booking.bookingId, booking.people))
                .from(booking)
                .leftJoin(booking.member, member)
                .where(booking.bookingId.eq(bookingId))
                .fetchOne();
    }

    //예약번호로 예약된 객실 조회
    @Override
    public List<BookedRoomResponse> findBookedRoom(Long bookingId) {
        return queryFactory
                .select(new QBookedRoomResponse(bookedRoom.booking.bookingId, roomInfo.roomType, bookedRoom.bookingPrice, bookedRoom.count, room.date))
                .from(bookedRoom)
                .leftJoin(bookedRoom.room, room)
                .leftJoin(room.roomInfo, roomInfo)
                .where(bookedRoom.booking.bookingId.eq(bookingId))
                .fetch();
    }

    //내 예약 조회(페이징O)
    @Override
    public Page<BookingListResponseWithImage> findBookingByMemberId(MyReservationCond cond, Pageable pageable) {
        //1:N 관계를 제외한 나머지 조회
        JPAQuery<BookingListResponseWithImage> result = queryFactory
                .select(new QBookingListResponseWithImage(member.name, booking.arrivalDate, booking.departureDate,
                        booking.bookingDate, booking.status, booking.bookingId, booking.people))
                .from(booking)
                .leftJoin(booking.member, member)
                .where(member.id.eq(cond.getId()), bookingStatusEq(cond.getStatus()), bookingIdEq(cond.getBookingId()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        for(Sort.Order o : pageable.getSort()){
            PathBuilder pathBuilder = new PathBuilder(booking.getType(), booking.getMetadata());
            result.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(o.getProperty())));
            }
        List<BookingListResponseWithImage> content = result.fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(booking.count())
                .from(booking)
                .leftJoin(booking.member, member)
                .where(member.id.eq(cond.getId()), bookingStatusEq(cond.getStatus()), bookingIdEq(cond.getBookingId()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    //예약번호로 예약된 객실 조회
    @Override
    public List<BookedRoomResponseWithImage> findBookedRoomByMemberId(List<Long> bookingIds) {
        //1:N 관계인 bookedRooms 조회
        return queryFactory
                .select(new QBookedRoomResponseWithImage(bookedRoom.booking.bookingId, roomInfo.roomInfoId, roomInfo.roomType, bookedRoom.bookingPrice, bookedRoom.count, room.date))
                .from(bookedRoom)
                .leftJoin(bookedRoom.room, room)
                .leftJoin(room.roomInfo, roomInfo)
                .where(bookedRoom.booking.bookingId.in(bookingIds))
                .fetch();
    }

    //내 예약 상세보기
    @Override
    public BookingListResponseWithImage findBookingWithImage(Long bookingId) {
        //1:N 관계를 제외한 나머지 조회
        return queryFactory
                .select(new QBookingListResponseWithImage(member.name, booking.arrivalDate, booking.departureDate,
                        booking.bookingDate, booking.status, booking.bookingId, booking.people))
                .from(booking)
                .leftJoin(booking.member, member)
                .where(booking.bookingId.eq(bookingId))
                .fetchOne();
    }
    //예약번호로 예약된 객실 조회
    @Override
    public List<BookedRoomResponseWithImage> findBookedRoomWithImage(Long bookingId) {
        //1:N 관계인 bookedRooms 조회
        return queryFactory
                .select(new QBookedRoomResponseWithImage(bookedRoom.booking.bookingId, roomInfo.roomInfoId, roomInfo.roomType,
                        bookedRoom.bookingPrice, bookedRoom.count, room.date))
                .from(bookedRoom)
                .leftJoin(bookedRoom.room, room)
                .leftJoin(room.roomInfo, roomInfo)
                .where(bookedRoom.booking.bookingId.eq(bookingId))
                .fetch();
    }

    //정렬방식
    private OrderSpecifier[] createOrderSpecifier(BookingSearchCondition condition){
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        if(condition.getArrivalDateFrom() != null && condition.getArrivalDateUntil() == null){
            orderSpecifiers.add(OrderByNull.DEFAULT);
        }else if(condition.getArrivalDateFrom() != null && condition.getArrivalDateUntil() != null){
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, booking.arrivalDate));
        }else if(condition.getDepartureDateFrom() != null && condition.getDepartureDateUntil() == null){
            orderSpecifiers.add(OrderByNull.DEFAULT);
        }else if(condition.getDepartureDateFrom() != null && condition.getDepartureDateUntil() != null){
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, booking.departureDate));
        }else if(condition.getBookingDateFrom() != null && condition.getBookingDateUntil() == null){
            orderSpecifiers.add(OrderByNull.DEFAULT);
        }else if(condition.getBookingDateFrom() != null && condition.getBookingDateUntil() != null){
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, booking.bookingDate));
        }else if(condition.getBookingId() != null) {
            orderSpecifiers.add(OrderByNull.DEFAULT);
        }else if(condition.getStayDateFrom() != null){
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, booking.arrivalDate));
        }else{
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, booking.bookingDate));
        }
        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }

    //동적쿼리(where 다중 파라미터 사용)
    private BooleanExpression memberNameEq(String memberName) {
        return isEmpty(memberName) ? null : booking.member.name.contains(memberName);
    }
    private BooleanExpression arrivalDateBetween(LocalDateTime arrivalDateFrom, LocalDateTime arrivalDateUntil) {
        return arrivalDateFrom == null ? null : arrivalDateUntil == null ? booking.arrivalDate.eq(arrivalDateFrom) : booking.arrivalDate.between(arrivalDateFrom, arrivalDateUntil);
    }
    private BooleanExpression departureDateBetween(LocalDateTime departureDateFrom, LocalDateTime departureDateUntil) {
        return departureDateFrom == null ? null : departureDateUntil == null ? booking.departureDate.eq(departureDateFrom) : booking.departureDate.between(departureDateFrom, departureDateUntil);
    }
    private BooleanExpression bookingDateBetween(LocalDateTime bookingDateFrom, LocalDateTime bookingDateUntil) {
        StringTemplate dateFrom = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, '%Y-%m-%d')", bookingDateFrom);
        StringTemplate dateUntil = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, '%Y-%m-%d')", bookingDateUntil);
        StringTemplate bookingDate = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, '%Y-%m-%d')", booking.bookingDate);
        return bookingDateFrom == null ? null : bookingDateUntil == null ? bookingDate.eq(dateFrom) : bookingDate.between(dateFrom, dateUntil);
    }
    private BooleanExpression bookingIdEq(Long bookingId) {
        return bookingId == null ? null : booking.bookingId.eq(bookingId);
    }
    private BooleanExpression stayOverBetween(LocalDateTime stayDateFrom, LocalDateTime stayDateUntil){
        return stayDateFrom == null? null : stayDateUntil == null ?
                booking.arrivalDate.eq(stayDateFrom).or(booking.arrivalDate.lt(stayDateFrom).and(booking.departureDate.gt(stayDateFrom)))
                : booking.arrivalDate.eq(stayDateFrom)
                .or(booking.arrivalDate.eq(stayDateUntil))
                .or(booking.arrivalDate.lt(stayDateFrom).and(booking.departureDate.gt(stayDateFrom)))
                .or(booking.arrivalDate.between(stayDateFrom, stayDateUntil));
    }
    private BooleanExpression bookingStatusEq(BookingStatus status){
        return status == null ? null : booking.status.eq(status);
    }
}
