package com.hotel.reservation.web.myPage.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CheckPasswordForm {
    private String email;
    @NotBlank
    private String password;
}
