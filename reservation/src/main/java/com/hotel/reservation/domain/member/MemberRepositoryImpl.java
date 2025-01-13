package com.hotel.reservation.domain.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import static com.hotel.reservation.domain.member.QMember.member;

public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    public MemberRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    //로그인
    @Override
    public findMemberDto login(String email, String password) {
        return queryFactory
                .select(new QfindMemberDto(member.id, member.name))
                .from(member)
                .where(member.email.eq(email), member.password.eq(password))
                .fetchOne();
    }
    //중복 email 체크
    @Override
    public Long findByEmail(String email) {
        return queryFactory
                .select(member.count())
                .from(member)
                .where(member.email.eq(email))
                .fetchOne();
    }
}
