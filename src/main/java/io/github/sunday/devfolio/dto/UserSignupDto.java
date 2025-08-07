package io.github.sunday.devfolio.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupDto {
    private String loginId;
    private String nickname;
    private String password;
    private String passwordConfirm;
}