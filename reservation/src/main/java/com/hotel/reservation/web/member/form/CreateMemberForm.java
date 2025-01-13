package com.hotel.reservation.web.member.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class CreateMemberForm {
    @NotBlank
    private String name;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$")
    private String email;

    @NotBlank
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}")
    private String password;

    @NotBlank
    @Pattern(regexp = "^01([0|1|6|7|8|9])[ -]?([0-9]{3,4})[ -]?([0-9]{4})$")
    private String phoneNumber;
}
