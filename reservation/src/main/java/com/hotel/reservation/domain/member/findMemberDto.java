package com.hotel.reservation.domain.member;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class findMemberDto {
    private Long id;
    private String name;

    public findMemberDto() {
    }

    @QueryProjection
    public findMemberDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
