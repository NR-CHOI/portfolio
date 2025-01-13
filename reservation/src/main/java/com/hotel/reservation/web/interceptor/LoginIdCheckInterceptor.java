package com.hotel.reservation.web.interceptor;

import com.hotel.reservation.domain.member.findMemberDto;
import com.hotel.reservation.domain.room.RoomService;
import com.hotel.reservation.web.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@RequiredArgsConstructor
public class LoginIdCheckInterceptor implements HandlerInterceptor {
    private final RoomService roomService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String[] url = request.getRequestURI().split("/");
        Long roomId =  Long.parseLong(url[url.length - 1]);
        log.info("roomId={}", roomId);

        HttpSession session = request.getSession(false);
        findMemberDto loginMember = (findMemberDto) session.getAttribute(SessionConst.LOGIN_MEMBER);

        if (roomService.findRegisterId(roomId) != loginMember.getId()) {
            log.info("권한없는 사용자의 접근");
            throw new RuntimeException("유효하지 않은 접근");
        }
        return true;
    }
}
