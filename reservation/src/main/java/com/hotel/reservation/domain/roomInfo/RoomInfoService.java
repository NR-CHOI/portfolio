package com.hotel.reservation.domain.roomInfo;

import com.hotel.reservation.domain.member.Member;
import com.hotel.reservation.domain.member.MemberRepository;
import com.hotel.reservation.domain.room.Room;
import com.hotel.reservation.domain.room.RoomRepository;
import com.hotel.reservation.domain.roomInfoImage.RoomInfoImage;
import com.hotel.reservation.domain.roomInfoImage.RoomInfoImageRepository;
import com.hotel.reservation.domain.s3.S3Service;
import com.hotel.reservation.web.roomInfo.form.RoomInfoSaveRequest;
import com.hotel.reservation.web.roomInfo.form.RoomInfoUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomInfoService {
    private final RoomInfoRepository roomInfoRepository;
    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;
    private final RoomInfoImageRepository roomInfoImageRepository;

    //객실정보 등록
    @Transactional
    public void addRoom(RoomInfoSaveRequest dto) throws IOException {
        List<RoomInfoImage> storeImageFiles = null;
        RoomInfo roomInfo = null;
        Member findMember = memberRepository.findById(dto.getId()).orElseThrow(NoSuchElementException::new);

        if (dto.getImageFiles() != null) {
            storeImageFiles = s3Service.storeFiles(dto.getImageFiles());
            roomInfo = RoomInfo.createRoomInfo(dto.getRoomNo(), dto.getRoomType(), (int) dto.getMaxNum(), findMember, storeImageFiles);
        } else {
            roomInfo = RoomInfo.createRoomInfoWithNoFile(dto.getRoomNo(), dto.getRoomType(), (int) dto.getMaxNum(), findMember);
        }
        roomInfoRepository.save(roomInfo);
    }

    //객실정보 수정
    @Transactional
    public void updateRoom(RoomInfoUpdateRequest dto, Long roomInfoId) throws IOException {
        RoomInfo findRoomInfo = roomInfoRepository.findById(roomInfoId).orElseThrow(NoSuchElementException::new);
        List<RoomInfoImage> imageFiles = findRoomInfo.getRoomInfoImages();
        //삭제버튼 누른 이미지파일 삭제하기
        String deleteImages = dto.getDeleteImage();
        if (deleteImages != null) {
            String[] imageIds = deleteImages.split(",");
            for (String imageId : imageIds) {
                if (imageId != null && !imageId.isEmpty()) {
                    long id = Long.parseLong(imageId);
                    for (RoomInfoImage imageFile : imageFiles) {
                        if (imageFile.getImageId() == id) {
                            //1. S3에 저장되어 있는 이미지파일 삭제
                            s3Service.deleteFile(imageFile.getStoreFileName());
                        }
                    }
                    //2. roominfo - roomInfoImage 연관관계 끊기
                    //removeIf(): List 특정값 삭제
                    imageFiles.removeIf(image -> image.getImageId() == id);
                    //3. roomInfoImage 삭제(연관관계 먼저 끊고 삭제해야함. 연관관계 맺은 상태에서 삭제하면 삭제 안됨.)
                    roomInfoImageRepository.deleteById(id);
                }
            }
        }
        //새로 추가한 이미지파일 update 하기
        if (dto.getImageFiles() != null) {
            List<RoomInfoImage> newFiles = s3Service.storeFiles(dto.getImageFiles());
            findRoomInfo.updateImages(newFiles);
        }
        findRoomInfo.update(dto.getRoomNo(), dto.getRoomType(), dto.getMaxNum());
    }

    //객실정보 삭제
    @Transactional
    public void deleteRoom(Long roomInfoId) {
        RoomInfo findRoomInfo = roomInfoRepository.findById(roomInfoId)
                .orElseThrow(()-> new NoSuchElementException("해당하는 객실유형이 없습니다."));
        //해당 roomInfo 유형인 room 재고들 삭제
        List<Room> rooms = roomRepository.findByRoomInfo(findRoomInfo);
        if (!rooms.isEmpty()) {
            rooms.forEach(room -> roomRepository.delete(room));
        }
        //S3에서 이미지파일 삭제
        List<RoomInfoImage> imageFiles = findRoomInfo.getRoomInfoImages();
        if (!imageFiles.isEmpty()) {
            imageFiles.forEach(i -> s3Service.deleteFile(i.getStoreFileName()));
        }
        //roomInfo 삭제
        roomInfoRepository.delete(findRoomInfo);
    }

    //roomInfoId로 객실유형 검색
    public RoomInfo findById(Long roomInfoId) {
        return roomInfoRepository.findById(roomInfoId).orElseThrow(()-> new NoSuchElementException("해당하는 객실유형이 없습니다."));
    }

    //등록자별 객실 전체검색
    public List<RoomInfoDto> findByMemberId(Long id) {
        return roomInfoRepository.findByMemberId(id);
    }

    //관리자가 등록한 모든 객실유형(페이징O)
    public Page<RoomInfo> findAllByMemberId(Long id, Pageable pageable) {
        return roomInfoRepository.findAllByMemberId(id, pageable);
    }

}
