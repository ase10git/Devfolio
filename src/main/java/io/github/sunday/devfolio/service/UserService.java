package io.github.sunday.devfolio.service;

import io.github.sunday.devfolio.entity.table.user.User;

public interface UserService {
    User findByUserIdx(Long userIdx);
    boolean isLoginIdDuplicate(String loginId);
    boolean isNicknameDuplicate(String nickname);
    boolean isValidLoginId(String loginId);
    boolean isValidNickname(String nickname);
    boolean isValidPassword(String password);
    void saveUser(User user);
    User findByLoginId(String loginId);
}
