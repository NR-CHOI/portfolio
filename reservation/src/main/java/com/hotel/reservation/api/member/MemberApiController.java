package com.hotel.reservation.api.member;

import com.hotel.reservation.domain.member.Member;
import com.hotel.reservation.domain.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {
    private final MemberService memberService;

    @PostMapping("/join")
    public CreateMemberResponse join(@RequestBody @Validated CreateMemberRequest request){
        if(memberService.duplicateCheck(request.getEmail()) != null){
            throw new RuntimeException("중복된 email입니다.");
        }

        Member member = Member.createMember(request.getName(), request.getEmail(), request.getPassword(), request.getPhoneNumber());
        Long id = memberService.save(member);
        return new CreateMemberResponse(id);
    }
}
