package com.hotel.reservation.web.interceptor;

import com.hotel.reservation.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("로그인 체크 인터셉터 실행 = {}", requestURI);

        String queryString = request.getQueryString();
        String encode = UriUtils.encode(queryString, StandardCharsets.UTF_8);

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
            if (queryString == null) {
                log.info("미인증 사용자 요청");
                response.sendRedirect("/login?redirectURL=" + requestURI);
            } else {
                log.info("미인증 사용자 요청: /login?redirectURL=" + requestURI + "?" + encode);
                response.sendRedirect("/login?redirectURL=" + requestURI + "?" + encode);
            }
            return false;
        }
        return true;
    }
}
