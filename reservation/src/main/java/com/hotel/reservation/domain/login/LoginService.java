package com.hotel.reservation.domain.login;

import com.hotel.reservation.domain.member.Member;
import com.hotel.reservation.domain.member.MemberRepository;
import com.hotel.reservation.domain.member.findMemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;

    public findMemberDto login(String email, String password){
        return memberRepository.login(email, password);
    }
}
