package com.hotel.reservation.web.booking;

import com.hotel.reservation.domain.booking.BookingSearchCondition;
import com.hotel.reservation.domain.booking.BookingService;
import com.hotel.reservation.domain.member.Member;
import com.hotel.reservation.domain.member.MemberService;
import com.hotel.reservation.domain.member.findMemberDto;
import com.hotel.reservation.domain.room.RoomService;
import com.hotel.reservation.web.argumentresolver.Login;
import com.hotel.reservation.web.booking.form.BookingForm;
import com.hotel.reservation.domain.booking.BookingListResponse;
import com.hotel.reservation.web.paging.PageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final RoomService roomService;
    private final MemberService memberService;

    //예약 신청
    @GetMapping("/add")
    public String addForm(@ModelAttribute("booking") BookingForm form, @Login findMemberDto loginMember, Model model) {
        Member findMember = memberService.findById(loginMember.getId());
        model.addAttribute("member", findMember);
        List<BookingForm> bookingFormList = form.getBookingFormList();
        model.addAttribute("bookingFormList", bookingFormList);
        return "booking/bookingForm";
    }

    @PostMapping("/add")
    public String addBooking(@ModelAttribute("booking") BookingForm form, @Login findMemberDto loginMember, Model model) {
        model.addAttribute("member", loginMember);
        List<BookingForm> bookingFormList = form.getBookingFormList();
        bookingService.Booking(bookingFormList);
        return "redirect:/booking/success";
    }

    //예약 성공시 메시지 출력
    @GetMapping("/success")
    public String bookingSuccess(@Login findMemberDto loginMember, Model model) {
        model.addAttribute("member", loginMember);
        return "booking/bookingSuccess";
    }

    //예약 리스트(관리자용)
    @GetMapping("/list")
    public String bookingList(@RequestParam(value = "page", defaultValue = "0") int page, HttpServletRequest request, @Login findMemberDto loginMember, Model model) {
        model.addAttribute("member", loginMember);
        String requestURI = request.getRequestURI();
        model.addAttribute("requestURI", requestURI);

        PageRequest pageRequest = PageRequest.of(page, 10);
        BookingSearchCondition condition = new BookingSearchCondition();
        condition.setId(loginMember.getId());

        Page<BookingListResponse> bookingList = bookingService.findAllBookings(condition, pageRequest);
        PageDto<BookingListResponse> pageDto = new PageDto<>(bookingList);

        model.addAttribute("bookingList", bookingList);
        model.addAttribute("pageDto", pageDto);
        return "booking/bookingList";
    }

    //예약 검색(관리자용)
    @GetMapping("/search")
    public String bookingSearch(@RequestParam Map<String, Object> data, @RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        String memberId = (String) data.get("memberId");
        log.info("memberId={}", memberId);
        Long id = Long.parseLong(memberId);

        String criteria = (String) data.get("criteria");
        String startDate = (String) data.get("startDate");
        String endDate = (String) data.get("endDate");
        String bookingName = (String) data.get("word");
        log.info("page={}", page);
        if (page != 0) {
            page = Integer.parseInt((String) data.get("page"));
        }

        PageRequest pageRequest = PageRequest.of(page, 10);
        BookingSearchCondition condition = new BookingSearchCondition();
        condition.setId(id);
        //검색 시작날짜, 끝날짜
        LocalDateTime dateFrom = null;
        LocalDateTime dateUntil = null;
        if (startDate != "") {
            dateFrom = LocalDate.parse(startDate).atStartOfDay();
        }
        if (endDate != "") {
            dateUntil = LocalDate.parse(endDate).atTime(LocalTime.MAX);
        }
        //검색기준이 예약번호일 때
        if (criteria.equals("bookingId")) {
            if (bookingName != "") {
                try {
                    condition.setBookingId(Long.parseLong(bookingName));
                } catch (NumberFormatException e) {   //예약번호로 검색시 숫자가 아닐 경우 처리
                    condition.setBookingId(null);
                }
            } else {  // 예약번호를 빈칸으로 검색시
                condition.setBookingId(null);
            }
            condition.setName(null);
        } else {    //검색기준이 예약번호가 아닐 때
            if (bookingName != "") {
                condition.setName(bookingName);
            }else{     //예약번호가 아닌 카테고리에서 빈칸으로 검색시
                condition.setName(null);
            }
        }
        //검색기준이 체크인 날짜일 때
        if (criteria.equals("arrivalDate")) {
            condition.setArrivalDateFrom(dateFrom);
            condition.setArrivalDateUntil(dateUntil);
        } else if (criteria.equals("departureDate")) {  //검색기준이 체크아웃 날짜일 때
            condition.setDepartureDateFrom(dateFrom);
            condition.setDepartureDateUntil(dateUntil);
        } else if (criteria.equals("bookingDate")) {    //검색기준이 예약 날짜일 때
            condition.setBookingDateFrom(dateFrom);
            condition.setBookingDateUntil(dateUntil);
        } else if (criteria.equals("stayOver")) {       //검색기준이 숙박중인 예약일 때
            condition.setStayDateFrom(dateFrom);
            condition.setStayDateUntil(dateUntil);
        }

        Page<BookingListResponse> bookingList = bookingService.findAllBookings(condition, pageRequest);
        PageDto<BookingListResponse> pageDto = new PageDto<>(bookingList);
        model.addAttribute("bookingList", bookingList);
        model.addAttribute("pageDto", pageDto);
        return "booking/bookingSearch";
    }

    //예약정보 상세보기(관리자용)
    @GetMapping("/view/{bookingId}")
    public String bookingView(@PathVariable("bookingId") Long bookingId, HttpServletRequest request, @Login findMemberDto loginMember, Model model) {
        model.addAttribute("member", loginMember);
        String requestURI = request.getRequestURI();
        model.addAttribute("requestURI", requestURI);

        BookingListResponse findBooking = bookingService.findBooking(bookingId);
        model.addAttribute("findBooking", findBooking);
        return "booking/bookingDetail";
    }

}
