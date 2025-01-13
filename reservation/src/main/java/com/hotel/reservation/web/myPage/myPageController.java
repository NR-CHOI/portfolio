package com.hotel.reservation.web.myPage;

import com.hotel.reservation.domain.booking.BookingService;
import com.hotel.reservation.domain.booking.BookingStatus;
import com.hotel.reservation.domain.booking.MyReservationCond;
import com.hotel.reservation.domain.login.LoginService;
import com.hotel.reservation.domain.member.Member;
import com.hotel.reservation.domain.member.MemberService;
import com.hotel.reservation.domain.member.findMemberDto;
import com.hotel.reservation.web.argumentresolver.Login;
import com.hotel.reservation.domain.booking.BookingListResponseWithImage;
import com.hotel.reservation.web.myPage.form.CheckPasswordForm;
import com.hotel.reservation.web.myPage.form.UpdateMemberForm;
import com.hotel.reservation.web.paging.PageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/myPage")
public class myPageController {
    private final MemberService memberService;
    private final LoginService loginService;
    private final BookingService bookingService;

    //회원정보수정을 위해 재로그인
    @GetMapping("/check")
    public String check(@ModelAttribute("form") CheckPasswordForm form, @Login findMemberDto loginMember, Model model) {
        model.addAttribute("member", loginMember);
        Member findMember = memberService.findById(loginMember.getId());
        form.setEmail(findMember.getEmail());
        return "myInfo/passwordCheck";
    }

    @PostMapping("/checkPassword")
    public String checkPassword(@Validated @ModelAttribute("form") CheckPasswordForm form, BindingResult bindingResult,
                                @Login findMemberDto loginMember, HttpServletRequest request, Model model) {
        if (bindingResult.hasErrors()) {
            log.info("error={}", bindingResult);
            model.addAttribute("member", loginMember);
            Member findMember = memberService.findById(loginMember.getId());
            form.setEmail(findMember.getEmail());
            return "myInfo/passwordCheck";
        }
        model.addAttribute("member", loginMember);
        findMemberDto checkMember = loginService.login(form.getEmail(), form.getPassword());
        if (checkMember == null) {
            bindingResult.reject("loginError", "비밀번호가 맞지 않습니다.");
            return "myInfo/passwordCheck";
        }
        request.getSession().setAttribute("checkPassword", checkMember);
        return "redirect:/myPage/myInfo";
    }

    //회원정보 수정
    @GetMapping("/myInfo")
    public String myInfo(@ModelAttribute("form") UpdateMemberForm form, HttpServletRequest request, @Login findMemberDto loginMember, Model model) {
        if (request.getSession(false).getAttribute("checkPassword") == null) {
            return "redirect:/myPage/check";
        }
        request.getSession(false).removeAttribute("checkPassword");
        model.addAttribute("member", loginMember);
        Member findMember = memberService.findById(loginMember.getId());
        model.addAttribute("form", findMember);
        return "myInfo/myInfo";
    }

    @PostMapping("/modifyInfo")
    public String modifyInfo(@Validated @ModelAttribute("form") UpdateMemberForm form, BindingResult bindingResult, @Login findMemberDto loginMember, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("member", loginMember);
            log.info("errors={}", bindingResult);
            return "myInfo/myInfo";
        }
        memberService.update(form);
        return "redirect:/myPage/success";
    }

    //회원정보 수정 성공시 메시지
    @GetMapping("/success")
    public String modifySuccess(@Login findMemberDto loginMember, Model model) {
        model.addAttribute("member", loginMember);
        return "myInfo/modifySuccess";
    }

    //내 예약 목록보기
    @GetMapping("/myReservation")
    public String reservation(@RequestParam(value = "status", defaultValue = "confirmed") String status,
                              @RequestParam(required = false) String range,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @Login findMemberDto loginMember, Model model) {
        model.addAttribute("member", loginMember);
        model.addAttribute("status", status);
        model.addAttribute("range", range);

        BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
        int size = 10;
        Page<BookingListResponseWithImage> bookingByMemberId = bookingByMemberId(page, size, range, loginMember, bookingStatus, null);
        model.addAttribute("myBookings", bookingByMemberId);

        PageDto<BookingListResponseWithImage> pageDto = new PageDto<>(bookingByMemberId);
        model.addAttribute("pageDto", pageDto);
        List<BookingListResponseWithImage> content = bookingByMemberId.getContent();
        model.addAttribute("content", content);
        return "myInfo/myReservation";
    }

    //내 예약 검색
    @GetMapping("/myReservationSearch")
    public String reservationSearch(@RequestParam(required = false) String status,
                                    @RequestParam(required = false) String range,
                                    @RequestParam(required = false) Long bookingId,
                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                    @Login findMemberDto loginMember, Model model) {
        model.addAttribute("member", loginMember);

        BookingStatus bookingStatus = null;
        if(status != null) {
            bookingStatus = BookingStatus.valueOf(status.toUpperCase());
        }
        int size = 10;
        Page<BookingListResponseWithImage> bookingByMemberId = bookingByMemberId(page, size, range, loginMember, bookingStatus, bookingId);
        model.addAttribute("myBookings", bookingByMemberId);

        PageDto<BookingListResponseWithImage> pageDto = new PageDto<>(bookingByMemberId);
        model.addAttribute("pageDto", pageDto);

        List<BookingListResponseWithImage> content = bookingByMemberId.getContent();
        model.addAttribute("content", content);
        return "myInfo/myReservationChange";
    }

    //중복코드는 메서드로
    private Page<BookingListResponseWithImage> bookingByMemberId(int page, int size, String range, findMemberDto loginMember,
                                                                 BookingStatus bookingStatus, Long bookingId){
        //기본 selected (정렬기준이 체크인날짜일 경우)
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "arrivalDate"));
        //정렬기준이 예약날짜일 경우
        if (range != null && range.equals("bookingDate")) {
            pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "bookingDate"));
        }
        MyReservationCond cond = new MyReservationCond(loginMember.getId(), bookingStatus, bookingId);
        return bookingService.findBookingByMemberId(cond, pageRequest);
    }

    //내 예약 상세보기
    @GetMapping("/reservationDetail/{bookingId}")
    public String reservationDetail(@PathVariable("bookingId") Long bookingId, @Login findMemberDto loginMember, Model model) {
        model.addAttribute("member", loginMember);
        BookingListResponseWithImage booking = bookingService.findBookingWithImage(bookingId);
        model.addAttribute("booking", booking);

        long stayDays = ChronoUnit.DAYS.between(booking.getArrivalDate(), booking.getDepartureDate());
        log.info("stayDays={}", stayDays);
        model.addAttribute("stayDays", stayDays);
        return "myInfo/reservationDetail";
    }

    //내 예약 취소
    @PostMapping("/cancel/{bookingId}")
    public String cancel(@PathVariable("bookingId") Long bookingId) {
        bookingService.cancel(bookingId);
        return "redirect:/myPage/myReservation";
    }
}


