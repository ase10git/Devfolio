package io.github.sunday.devfolio.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupDto {
    private String loginId;
    private String nickname;
    private String password;
    private String passwordConfirm;
    private String email;
}