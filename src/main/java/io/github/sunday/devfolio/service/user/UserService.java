package io.github.sunday.devfolio.service.user;

import io.github.sunday.devfolio.entity.table.user.User;

public interface UserService {
    User findByUserIdx(Long userIdx);
    boolean isLoginIdDuplicate(String loginId);
    boolean isNicknameDuplicate(String nickname);
    boolean isEmailDuplicate(String email);
    boolean isValidLoginId(String loginId);
    boolean isValidNickname(String nickname);
    boolean isValidPassword(String password);
    void saveUser(User user);
    User findByLoginId(String loginId);
    User findByEmail(String email);
    String generateValidNickname(String baseName);
    String generateUserId(String email);
}
