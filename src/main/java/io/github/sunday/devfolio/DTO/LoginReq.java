package io.github.sunday.devfolio.dto;

import lombok.Data;

@Data
public class LoginReq {
    private String loginId;
    private String password;
}