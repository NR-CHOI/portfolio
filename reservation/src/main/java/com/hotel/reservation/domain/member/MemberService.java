package com.hotel.reservation.domain.member;

import com.hotel.reservation.web.myPage.form.UpdateMemberForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    //회원가입
    @Transactional
    public Long save(Member member){
        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    //회원수정
    @Transactional
    public void update(UpdateMemberForm dto){
        Member findMember = memberRepository.findById(dto.getId()).orElse(null);
        if(findMember != null) {
            findMember.updateRoom(dto.getPassword(), dto.getPhoneNumber());
        }
    }

    //전체 회원조회
    public List<Member> findAll(){
        return memberRepository.findAll();
    }

    //이름 조회
    public List<Member> findByName(String name){
        return memberRepository.findByName(name);
    }

    //아이디 조회
    public Member findById(Long id){
        return memberRepository.findById(id).orElseThrow(()-> new NoSuchElementException("해당하는 아이디가 없습니다."));
    }

    //중복 email 체크
    public Long duplicateCheck(String email){
        return memberRepository.findByEmail(email);
    }
}
