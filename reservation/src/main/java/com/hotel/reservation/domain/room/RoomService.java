package com.hotel.reservation.domain.room;

import com.hotel.reservation.domain.roomInfo.RoomInfo;
import com.hotel.reservation.domain.roomInfo.RoomInfoRepository;
import com.hotel.reservation.domain.roomInfoImage.RoomInfoImage;
import com.hotel.reservation.domain.roomInfoImage.RoomInfoImageRepository;
import com.hotel.reservation.web.room.form.RoomUpdateForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomInfoRepository roomInfoRepository;
    private final RoomInfoImageRepository roomInfoImageRepository;

    //객실재고 업데이트
    @Transactional
    public void updateRoom(List<LocalDateTime> dates, RoomUpdateForm form) {
        RoomInfo findRoomInfo = roomInfoRepository.findById(form.getRoomInfoId()).orElseThrow(() -> new NoSuchElementException("해당하는 객실재고가 없습니다."));
        // 변경할 날짜에 기존날짜가 포함되어 있을 경우
        // 날짜와 객실타입이 일치하는 재고가 없으면 재고추가
        // 날짜와 객실타입이 일치하는 재고가 있으면 재고수정
        dates.forEach(date -> {
            Long findRoomId = updateCheck(date, findRoomInfo.getRoomInfoId());
            if (findRoomId == null) {
                //기존 재고가 없는 날짜는 insert
                RoomAddDto roomAddDto = new RoomAddDto(date, form.getPrice(), form.getQuantity(), form.getStatus(), findRoomInfo);
                Room room = Room.createRoom(roomAddDto);
                roomRepository.save(room);
            } else {
                RoomUpdateDto dto = new RoomUpdateDto(date, form.getPrice(), form.getQuantity(), form.getStatus(), findRoomInfo);
                //기존 재고가 있는 날짜는 update
                Room findRoom = roomRepository.findById(findRoomId).orElseThrow(() -> new NoSuchElementException("해당하는 객실재고가 없습니다."));
                findRoom.updateRoom(dto);
            }
        });
    }

    //해당 날짜와 객실유형에 일치하는 객실재고가 있는지 체크하기
    private Long updateCheck(LocalDateTime date, Long roomInfoId) {
        Long findRoomId = roomRepository.findByDateAndRoomInfo(date, roomInfoId);
        if(findRoomId == null){
            return null;
        }
        return findRoomId;
    }

    //객실삭제
    @Transactional
    public void deleteRoom(Long roomId) {
        Room findRoom = roomRepository.findById(roomId).orElseThrow(NoSuchElementException::new);
        roomRepository.delete(findRoom);
    }

    //객실 아이디로 검색
    public Room findById(Long roomId) {
        return roomRepository.findById(roomId).orElse(null);
    }

    //객실재고 등록한 아이디 검색
    public Long findRegisterId(Long id) {
        return roomRepository.findRegisterId(id);
    }

    //년, 월, 객실유형에 일치하는 객실재고 데이터 가져오기(달력형, 관리자용)
    public List<CalendarDto> findByYearAndMonthAndRoomType(LocalDateTime date, Long id, Long roomInfoId){
        return roomRepository.findByYearAndMonthAndRoomType(date, id, roomInfoId);
    }

    //등록자별 객실 전체검색(목록형, 관리자용, 페이징O)
    public Page<RoomByMemberDto> findByMemberId(Long id, Pageable pageable) {
        return roomRepository.findByMemberId(id, pageable);
    }

    //객실재고 검색(페이징O, 예약하기 위한 검색용)
    public Slice<RoomSearchResult> search(RoomSearchDto dto, Pageable pageable){
        Slice<RoomSearchResult> results = roomRepository.search(dto, pageable);
        //roomInfoId로 roomInfoImage 한번에 조회
        Map<Long, List<String>> images = roomInfoImageRepository.findByRoomInfoId(roomInfoIds(results));
        //roomInfoId를 넣으면 해당 storeS3Url만 추출
        results.forEach(r->r.setStoreS3Urls(images.get(r.getRoomInfoId())));
        return results;
    }
    //roomInfoId만 추출
    private Set<Long> roomInfoIds(Slice<RoomSearchResult> results){
        return results.stream().map(RoomSearchResult::getRoomInfoId).collect(Collectors.toSet());
    }

    //해당조건 && roomInfoId가 일치하는 객실재고 가져오기
    public List<RoomSearchRoomsResult> searchRooms(RoomSearchDto dto, List<Long> roomInfoId){
        return roomRepository.searchRooms(dto.getCheckInDate(), dto.getCheckOutDate(), dto.getQuantity(), dto.getPeople(), roomInfoId);
    }
}


