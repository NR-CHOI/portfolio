package com.hotel.reservation.web;

import com.hotel.reservation.domain.member.findMemberDto;
import com.hotel.reservation.domain.room.RoomSearchDto;
import com.hotel.reservation.domain.room.RoomSearchResult;
import com.hotel.reservation.domain.room.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final RoomService roomService;

    //메인 페이지
    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {
        //메인화면에 추천숙소 정보 가져오기
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = LocalDate.now().plusDays(1);
        int quantity = 1;
        int people = 2;
        long stayDate = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        RoomSearchDto dto = new RoomSearchDto(quantity, people, checkInDate.atStartOfDay(), checkOutDate.atStartOfDay(), stayDate);
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "roomId"));
        Slice<RoomSearchResult> result = roomService.search(dto, pageRequest);
        model.addAttribute("result", result);
        model.addAttribute("checkInDate", checkInDate);
        model.addAttribute("checkOutDate", checkOutDate);
        model.addAttribute("quantity", quantity);
        model.addAttribute("people", people);

        HttpSession session = request.getSession(false);
        if(session == null){
            return "home";  //세션이 없으면 홈으로 리다이렉트
        }
        findMemberDto loginMember = (findMemberDto) session.getAttribute("loginMember");
        if(loginMember == null){
            return "home";  //로그인하지 않은 경우 홈으로 리다이렉트
        }
        model.addAttribute("member", loginMember);
        return "home";
    }
}
