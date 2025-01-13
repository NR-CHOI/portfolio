package com.hotel.reservation.web.login;

import com.hotel.reservation.domain.login.LoginService;
import com.hotel.reservation.domain.member.findMemberDto;
import com.hotel.reservation.web.SessionConst;
import com.hotel.reservation.web.login.form.LoginForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class loginController {
    private final LoginService loginService;

    //로그인
    @GetMapping("/login")
    public String loginForm(@ModelAttribute("member") LoginForm form, HttpServletRequest request) {
        String queryString = request.getQueryString();
        //직접 로그인버튼을 눌러 로그인 창으로 이동한 경우
        if (queryString == null) {
            //현재 페이지 오기 전 URI정보
            String uri = request.getHeader("Referer");
            log.info("prevPage={}", uri);
            if (uri != null && !uri.contains("/login") && !uri.contains("/member") ) {
                request.getSession().setAttribute("prevPage", uri);
            }
        }
        return "login/loginForm";
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("member") LoginForm form, BindingResult bindingResult,
                        @RequestParam(defaultValue = "/") String redirectURL, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "login/loginForm";
        }

        findMemberDto loginMember = loginService.login(form.getEmail(), form.getPassword());
        if (loginMember == null) {
            bindingResult.reject("loginError", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        //직접 로그인 버튼 눌러서 로그인한 경우, 원래 페이지로 이동
        String prevPage = (String) request.getSession(false).getAttribute("prevPage");
        if (prevPage != null) {
            redirectURL = prevPage;
            request.getSession().removeAttribute("prevPage");
        }
        return "redirect:" + redirectURL;
    }

    //로그아웃
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
