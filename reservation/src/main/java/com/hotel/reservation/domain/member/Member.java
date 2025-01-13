package com.hotel.reservation.domain.member;

import com.hotel.reservation.domain.booking.Booking;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;

    @OneToMany(mappedBy = "member")
    private List<Booking> bookings = new ArrayList<>();
    private Member(String name, String email, String password, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
    public static Member createMember(String name, String email, String password, String phoneNumber){
        Member member = new Member(name, email, password, phoneNumber);
        return member;
    }

    public void updateRoom(String password, String phoneNumber){
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
}
