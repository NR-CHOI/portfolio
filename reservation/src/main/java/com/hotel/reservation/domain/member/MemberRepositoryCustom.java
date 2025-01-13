package com.hotel.reservation.domain.member;

public interface MemberRepositoryCustom {
    //로그인
    findMemberDto login(String email, String password);
    //중복 email 체크
    Long findByEmail(String email);
}
