package com.hotel.reservation.web.roomInfo;

import com.hotel.reservation.domain.member.findMemberDto;
import com.hotel.reservation.domain.roomInfo.RoomInfo;
import com.hotel.reservation.domain.roomInfo.RoomInfoService;
import com.hotel.reservation.domain.roomInfoImage.RoomInfoImage;
import com.hotel.reservation.web.argumentresolver.Login;
import com.hotel.reservation.web.paging.PageDto;
import com.hotel.reservation.web.roomInfo.form.RoomInfoSaveRequest;
import com.hotel.reservation.web.roomInfo.form.RoomInfoUpdateForm;
import com.hotel.reservation.web.roomInfo.form.RoomInfoUpdateRequest;
import com.hotel.reservation.web.roomInfo.form.RoomInfoViewForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/roominfo")
@RequiredArgsConstructor
public class RoomInfoController {
    private final RoomInfoService roomInfoService;

    //객실유형 등록
    @GetMapping("/add")
    public String addForm(@ModelAttribute("roomInfo") RoomInfoSaveRequest form, @Login findMemberDto loginMember, Model model) {
        model.addAttribute("member", loginMember);
        return "roominfo/addForm";
    }

    @PostMapping("/add")
    public String addRoom(@Validated @ModelAttribute("roomInfo") RoomInfoSaveRequest form, BindingResult bindingResult, @Login findMemberDto loginMember, Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            log.info("error={}", bindingResult);
            model.addAttribute("member", loginMember);
            return "roominfo/addForm";
        }
       roomInfoService.addRoom(form);
        return "redirect:/roominfo/addMessage";
    }

    //객실유형 수정
    @GetMapping("/update/{roomInfoId}")
    public String updateRoomForm(@PathVariable("roomInfoId") Long roomInfoId, @Login findMemberDto loginMember, Model model) {
        model.addAttribute("member", loginMember);
        RoomInfo findRoomInfo = roomInfoService.findById(roomInfoId);
        RoomInfoUpdateForm form = new RoomInfoUpdateForm(findRoomInfo.getRoomNo(), findRoomInfo.getRoomType(), findRoomInfo.getMaxNum(), findRoomInfo.getRoomInfoImages());
        model.addAttribute("roominfo", form);
        return "roominfo/updateForm";
    }

    @PostMapping("/update/{roomInfoId}")
    public String updateRoom(@Validated @ModelAttribute("roominfo") RoomInfoUpdateRequest dto, BindingResult bindingResult,
                             @PathVariable("roomInfoId") Long roomInfoId, @Login findMemberDto loginMember, Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            log.info("error={}", bindingResult);
            model.addAttribute("member", loginMember);
            RoomInfo findRoomInfo = roomInfoService.findById(roomInfoId);
            List<RoomInfoImage> images = findRoomInfo.getRoomInfoImages();
            model.addAttribute("images", images);
            return "roominfo/updateForm";
        }
        roomInfoService.updateRoom(dto, roomInfoId);
        return "redirect:/roominfo/list";
    }

    //객실유형 삭제
    @PostMapping("/delete/{roomInfoId}")
    public String deleteRoom(@PathVariable("roomInfoId") Long roomInfoId) {
        roomInfoService.deleteRoom(roomInfoId);
        return "redirect:/roominfo/list";
    }

    //객실유형 상세보기
    @GetMapping("/view/{roomInfoId}")
    public String view(@Login findMemberDto loginMember, @PathVariable("roomInfoId") Long roomInfoId, Model model) {
        model.addAttribute("member", loginMember);
        RoomInfo findRoomInfo = roomInfoService.findById(roomInfoId);
        RoomInfoViewForm form = new RoomInfoViewForm(findRoomInfo.getRoomInfoId(), findRoomInfo.getRoomNo(), findRoomInfo.getRoomType(), findRoomInfo.getMaxNum(), findRoomInfo.getRoomInfoImages());
        model.addAttribute("roomInfo", form);
        return "roominfo/roomView";
    }

    //객실유형 목록보기
    @GetMapping("/list")
    public String roomList(@RequestParam(value = "page", defaultValue = "0") int page, @Login findMemberDto loginMember, Model model) {
        model.addAttribute("member", loginMember);
        log.info("page={}", page);
        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<RoomInfo> roomInfoList = roomInfoService.findAllByMemberId(loginMember.getId(), pageRequest);
        PageDto<RoomInfo> pageDto = new PageDto<>(roomInfoList);
        model.addAttribute("roomInfoList", roomInfoList);
        model.addAttribute("pageDto", pageDto);
        return "roominfo/roomList";
    }

    //객실유형 등록 성공시 출력되는 메시지
    @GetMapping("/addMessage")
    public String addRoomComplete(@Login findMemberDto loginMember, Model model) {
        model.addAttribute("member", loginMember);
        return "roominfo/addMessage";
    }
}
