package com.hotel.reservation.web.member;

import com.hotel.reservation.domain.member.Member;
import com.hotel.reservation.domain.member.MemberService;
import com.hotel.reservation.web.member.form.CreateMemberForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    //회원가입
    @GetMapping("/join")
    public String addForm(@ModelAttribute("member") CreateMemberForm form) {
        return "member/addForm";
    }

    @PostMapping("/join")
    public String addMember(@Validated @ModelAttribute("member") CreateMemberForm form, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "member/addForm";
        }
        //중복 email 체크
        if (memberService.duplicateCheck(form.getEmail()) != 0) {
            bindingResult.reject("duplicatedEmail", "사용할 수 없는 이메일입니다. 다른 이메일을 입력해 주세요.");
            return "member/addForm";
        }
        //회원저장
        Member member = Member.createMember(form.getName(), form.getEmail(), form.getPassword(), form.getPhoneNumber());
        Long memberId = memberService.save(member);
        //회원가입 후, memberId 세션에 저장
        HttpSession session = request.getSession();
        session.setAttribute("joinMember", memberId);
        return "redirect:/member/addMessage";
    }

    //회원가입 성공 후 메시지 출력
    @GetMapping("/addMessage")
    public String addComplete(HttpServletRequest request, Model model) {
        //세션에서 memberId 찾아오기
        HttpSession session = request.getSession(false);
        Long memberId = (Long) session.getAttribute("joinMember");
        //memberId로 이름을 찾아와 출력
        Member findMember = memberService.findById(memberId);
        model.addAttribute("memberName", findMember.getName());
        return "member/addMessage";
    }
}
