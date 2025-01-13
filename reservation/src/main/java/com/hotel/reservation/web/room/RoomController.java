package com.hotel.reservation.web.room;

import com.hotel.reservation.domain.member.findMemberDto;
import com.hotel.reservation.domain.room.*;
import com.hotel.reservation.domain.roomInfo.RoomInfoDto;
import com.hotel.reservation.domain.roomInfo.RoomInfoService;
import com.hotel.reservation.web.argumentresolver.Login;
import com.hotel.reservation.web.paging.PageDto;
import com.hotel.reservation.web.room.form.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Controller
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private final RoomInfoService roomInfoService;

    //객실재고 등록
    @GetMapping("/add")
    public String addForm(@ModelAttribute("room") RoomUpdateForm form, @ModelAttribute RoomSaveDto dto,
                          @Login findMemberDto loginMember, Model model) {
        model.addAttribute("member", loginMember);
        //해당 아이디가 등록한 객실유형 정보
        List<RoomInfoDto> roomInfos = roomInfoService.findByMemberId(loginMember.getId());
        model.addAttribute("roomInfos", roomInfos);
        //클릭한 날짜와 객실유형 데이터 넘기기
        LocalDate startDate = LocalDate.of(dto.getYear(), dto.getMonth(), dto.getDay());
        RoomUpdateForm room = new RoomUpdateForm(startDate, dto.getRoomInfoId());
        model.addAttribute("room", room);
        return "room/addForm";
    }

    @PostMapping("/add")
    public String addRoom(@Validated @ModelAttribute("room") RoomUpdateForm form, BindingResult bindingResult,
                          @Login findMemberDto loginMember, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.info("error={}", bindingResult);
            model.addAttribute("member", loginMember);
            List<RoomInfoDto> roomInfos = roomInfoService.findByMemberId(loginMember.getId());
            model.addAttribute("roomInfos", roomInfos);
            return "room/addForm";
        }

        //객실재고 등록할 날짜 컬렉션에 담기
        List<LocalDateTime> dates = dates(form.getStartDate(), form.getEndDate());
        roomService.updateRoom(dates, form);

        //변경 후 변경한 날짜와 유형에 해당하는 페이지(달력)로 리다이렉트하기 위해 데이터 보내기
        redirectAttributes.addAttribute("roomInfoId", form.getRoomInfoId());
        redirectAttributes.addAttribute("year", form.getStartDate().getYear());
        redirectAttributes.addAttribute("month", form.getStartDate().getMonth().getValue());
        return "redirect:/room/calendar";
    }

    //객실재고 수정
    @GetMapping("/update/{roomId}")
    public String updateRoomForm(@PathVariable("roomId") Long roomId, @Login findMemberDto loginMember, Model model) {
        model.addAttribute("member", loginMember);
        model.addAttribute("roomId", roomId);
        //수정할 객실재고 데이터 가져오기
        Room findRoom = roomService.findById(roomId);
        LocalDate startDate = findRoom.getDate().toLocalDate();
        RoomUpdateForm form = new RoomUpdateForm(startDate, null, findRoom.getPrice(), findRoom.getQuantity(), findRoom.getStatus(), findRoom.getRoomInfo().getRoomInfoId());
        model.addAttribute("room", form);
        //해당 아이디가 등록한 객실유형 정보
        List<RoomInfoDto> roomInfos = roomInfoService.findByMemberId(loginMember.getId());
        model.addAttribute("roomInfos", roomInfos);
        return "room/updateForm";
    }

    @PostMapping("/update/{roomId}")
    public String updateRoom(@Validated @ModelAttribute("room") RoomUpdateForm form, BindingResult bindingResult,
                             @PathVariable("roomId") Long roomId, @Login findMemberDto loginMember, Model model,
                             RedirectAttributes redirectAttributes) {
        log.info("form={}", form);
        if (bindingResult.hasErrors()) {
            log.info("error={}", bindingResult);
            model.addAttribute("member", loginMember);

            List<RoomInfoDto> roomInfos = roomInfoService.findByMemberId(loginMember.getId());
            model.addAttribute("roomInfos", roomInfos);
            return "room/updateForm";
        }

        //날짜 컬렉션에 담기
        List<LocalDateTime> dates = dates(form.getStartDate(), form.getEndDate());

        //변경할 날짜들 중에서 기존날짜가 포함되어 있는지 확인 후, 포함하지 않을 경우 기존날짜의 room(재고) 삭제
        Room findRoom = roomService.findById(roomId);
        if (dates.contains(findRoom.getDate()) == false) {
            roomService.deleteRoom(roomId);
        }
        roomService.updateRoom(dates, form);
        //변경 후 변경한 날짜와 유형에 해당하는 페이지(달력)로 리다이렉트하기 위해 데이터 보내기
        redirectAttributes.addAttribute("roomInfoId", form.getRoomInfoId());
        redirectAttributes.addAttribute("year", form.getStartDate().getYear());
        redirectAttributes.addAttribute("month", form.getStartDate().getMonth().getValue());
        return "redirect:/room/calendar";
    }

    //중복 코드는 메서드로
    private List<LocalDateTime> dates(LocalDate startDateFromForm, LocalDate endDateFromForm) {
        // 끝 날짜가 없을경우, 시작 날짜로 대체
        LocalDateTime startDate = LocalDateTime.of(startDateFromForm, LocalTime.of(0, 0));
        LocalDateTime endDate = null;
        if (endDateFromForm == null) {
            endDate = startDate;
        } else if (endDateFromForm != null) {
            endDate = LocalDateTime.of(endDateFromForm, LocalTime.of(0, 0));
        }
        //날짜들 컬렉션에 담기
        return Stream.iterate(startDate, localDateTime -> localDateTime.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startDate, endDate.plusDays(1)))
                .collect(Collectors.toList());
    }


    //객실재고 삭제
    @PostMapping("/delete/{roomId}")
    public String deleteRoom(@PathVariable("roomId") Long roomId) {
        roomService.deleteRoom(roomId);
        return "redirect:/room/list";
    }

    //객실재고 달력
    @GetMapping("/calendar")
    public String calender(@Login findMemberDto loginMember,
                           @RequestParam(required = false) Long roomInfoId,
                           @RequestParam(required = false, defaultValue = "0") int year,
                           @RequestParam(required = false, defaultValue = "0") int month,
                           Model model) {
        model.addAttribute("member", loginMember);

        LocalDateTime today = LocalDateTime.now();
        int dayOfMonth = today.getDayOfMonth();
        if (year == 0 || month == 0) {
            year = today.getYear();
            month = today.getMonth().getValue();
        }
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("dayOfMonth", dayOfMonth);

        // 해당회원이 등록한 객실타입(roomInfo) 검색
        List<RoomInfoDto> findRoomInfos = roomInfoService.findByMemberId(loginMember.getId());
        model.addAttribute("findRoomInfos", findRoomInfos);
        //(해당 회원이 등록한 객실타입이 없을 경우) roomInfoId 가 없고, 등록한 객실타입이 없을 경우 스킵
        if (findRoomInfos.size() == 0) {
            return "redirect:/roominfo/list";
        }
        // (해당 회원이 등록한 객실타입이 있을 경우) roomInfoId 가 없을 경우, 해당 회원이 등록한 객실타입 중 0번째 id 가져오기
        if (roomInfoId == null && findRoomInfos.size() != 0) {
            roomInfoId = findRoomInfos.get(0).getRoomInfoId();
        }
        model.addAttribute("roomInfoId", roomInfoId);

        //해당월 1일의 요일구하기(월:1~일:7)
        int startDay = LocalDateTime.of(year, month, 1, 0, 0).getDayOfWeek().getValue();

        //날짜에 해당하는 객실재고정보를 객체에 담기
        List<Calendar> calendarList = drawCalendar(loginMember, roomInfoId, year, month);

        //총 날짜 칸의 갯수가 7의 배수가 아닐 경우, 부족한 칸의 갯수를 계산하여 추가하기
        int roundUp = (int) Math.ceil((startDay + calendarList.size()) / 7.0);
        int needList = (7 * roundUp) - startDay - calendarList.size();
        model.addAttribute("needList", needList);
        model.addAttribute("calendarList", calendarList);
        model.addAttribute("startDay", startDay);
        return "room/calendar";
    }

    //객실재고 달력에서 월이 변경될 때
    @GetMapping("/calendarChange")
    public String calenderChange(@Login findMemberDto loginMember, @RequestParam Long roomInfoId, @RequestParam int year, @RequestParam int month, Model model) {
        model.addAttribute("member", loginMember);
        model.addAttribute("roomInfoId", roomInfoId);

        int startDay = LocalDateTime.of(year, month, 1, 0, 0).getDayOfWeek().getValue();
        List<Calendar> calendarList = drawCalendar(loginMember, roomInfoId, year, month);

        model.addAttribute("calendarList", calendarList);
        model.addAttribute("startDay", startDay);

        //오늘 낢짜 표시
        LocalDateTime today = LocalDateTime.now();
        int todayYear = today.getYear();
        int todayMonth = today.getMonth().getValue();
        int todayDay = today.getDayOfMonth();
        model.addAttribute("todayYear", todayYear);
        model.addAttribute("todayMonth", todayMonth);
        model.addAttribute("todayDay", todayDay);

        //총 날짜 칸의 갯수가 7의 배수가 아닐 경우, 부족한 칸의 갯수를 계산하여 추가하기
        int roundUp = (int) Math.ceil((startDay + calendarList.size()) / 7.0);
        int needList = (7 * roundUp) - startDay - calendarList.size();
        model.addAttribute("needList", needList);
        return "room/calendarContent";
    }

    //중복코드는 메서드로
    private List<Calendar> drawCalendar(findMemberDto loginMember, Long roomInfoId, int year, int month) {
        //해당 월의 마지막날짜 구하기
        int lengthOfMonth = YearMonth.of(year, month).lengthOfMonth();
        // 객실타입(roomInfo), 년, 월에 해당하는 객실정보 가져오기(roomId, 날짜, 룸타입, 가격, 수량, open 유무)
        List<CalendarDto> findRooms = roomService.findByYearAndMonthAndRoomType(LocalDateTime.of(year, month, 1, 0, 0), loginMember.getId(), roomInfoId);
        List<Calendar> calendarList = new ArrayList<>();
        //날짜에 해당하는 객실재고정보를 객체에 담기
        for (int day = 1; day <= lengthOfMonth; day++) {
            List<RoomCalendarList> rooms = new ArrayList<>();
            for (CalendarDto findRoomInfo : findRooms) {
                if (findRoomInfo.getDate().getDayOfMonth() == day) {
                    RoomCalendarList form = new RoomCalendarList(findRoomInfo.getRoomId(), findRoomInfo.getRoomType(), findRoomInfo.getPrice(), findRoomInfo.getQuantity(), findRoomInfo.getStatus());
                    rooms.add(form);
                }
            }
            Calendar calendar = new Calendar(year, month, day, rooms);
            calendarList.add(calendar);
        }
        return calendarList;
    }

    //객실재고 목록보기
    @GetMapping("/list")
    public String roomList(@RequestParam(value = "page", defaultValue = "0") int page,
                           @Login findMemberDto loginMember, Model model) {
        model.addAttribute("member", loginMember);

        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "roomId"));
        Page<RoomByMemberDto> findRooms = roomService.findByMemberId(loginMember.getId(), pageRequest);
        //페이징관련 정보
        PageDto<RoomByMemberDto> pageDto = new PageDto<>(findRooms);
        model.addAttribute("roomList", findRooms);
        model.addAttribute("pageDto", pageDto);
        return "room/list";
    }

    //객실재고 검색
    @GetMapping("/search")
    public String searchResult(@Validated @ModelAttribute("form") RoomSearchForm form, BindingResult bindingResult,
                               @Login findMemberDto loginMember, @RequestParam(value = "page", defaultValue = "0") int page,
                               Model model) {
        model.addAttribute("member", loginMember);
        if (bindingResult.hasErrors()) {
            log.info("error={}", bindingResult);
            return "home";
        }
        long stayDate = ChronoUnit.DAYS.between(form.getCheckInDate(), form.getCheckOutDate());
        RoomSearchDto dto = new RoomSearchDto(form.getQuantity(), form.getPeople(), form.getCheckInDate().atStartOfDay(), form.getCheckOutDate().atStartOfDay(), stayDate);

        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "roomId"));
        Slice<RoomSearchResult> result = roomService.search(dto, pageRequest);

        int nowPage = result.getPageable().getPageNumber();
        boolean hasNext = result.hasNext();
        List<RoomSearchResult> content = result.getContent();

        model.addAttribute("result", result);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("hasNext", hasNext);
        model.addAttribute("content", content);
        model.addAttribute("stayDate", stayDate);
        return "booking/roomList";
    }

    //객실재고 더보기 클릭했을 때
    @GetMapping("/searchMore")
    public String searchResultMore(@RequestParam Map<String, Object> data, @Login findMemberDto loginMember, Model model) {
        model.addAttribute("loginMember", loginMember);

        int page = Integer.parseInt((String) data.get("page"));
        String checkIn = (String) data.get("checkInDate");//yyyy-MM-dd
        String checkOut = (String) data.get("checkOutDate");
        int people = Integer.parseInt((String) data.get("people"));
        int quantity = Integer.parseInt((String) data.get("quantity"));
        Long stayDate = Long.parseLong((String) data.get("stayDate"));
        LocalDate checkInLocalDate = LocalDate.parse(checkIn);
        LocalDateTime checkInDate = LocalDate.parse(checkIn).atStartOfDay();
        LocalDate checkOutLocalDate = LocalDate.parse(checkOut);
        LocalDateTime checkOutDate = LocalDate.parse(checkOut).atStartOfDay();

        RoomSearchForm form = new RoomSearchForm(quantity, people, checkInLocalDate, checkOutLocalDate);
        model.addAttribute("form", form);

        RoomSearchDto dto = new RoomSearchDto(quantity, people, checkInDate, checkOutDate, stayDate);
        int size = 10;

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "roomId"));
        Slice<RoomSearchResult> result = roomService.search(dto, pageRequest);
        boolean hasNext = result.hasNext();
        int elementIndex = page * size;

        model.addAttribute("result", result);
        model.addAttribute("hasNext", hasNext);
        model.addAttribute("elementIndex", elementIndex);
        model.addAttribute("stayDate", stayDate);
        return "booking/roomListMore";
    }

//    @GetMapping("/addMessage")
//    public String addRoomComplete(@Login findMemberDto loginMember, Model model) {
//        model.addAttribute("member", loginMember);
//        return "room/addMessage";
//    }

}
