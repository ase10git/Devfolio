package io.github.sunday.devfolio.service.impl;

import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.repository.EmailVerificationRepository;
import io.github.sunday.devfolio.repository.UserRepository;
import io.github.sunday.devfolio.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

/**
 * {@link UserService} 인터페이스의 구현체로,
 * 사용자 관련 비즈니스 로직(회원 저장, 중복 검사, 유효성 검사 등)을 처리합니다.
 * <p>
 * 비밀번호는 {@link PasswordEncoder}를 통해 암호화되어 저장됩니다.
 * </p>
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationRepository emailVerificationRepository;

    /**
     * {@code UserServiceImpl} 생성자.
     * <p>
     * 사용자 데이터베이스 접근을 위한 {@link UserRepository}와,
     * 비밀번호 암호화를 위한 {@link PasswordEncoder}를 주입받습니다.
     * </p>
     *
     * @param userRepository 사용자 엔티티에 대한 CRUD 처리를 담당하는 JPA 리포지토리
     * @param passwordEncoder 비밀번호 암호화에 사용되는 Spring Security 제공 인코더 (BCrypt)
     */
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailVerificationRepository emailVerificationRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.emailVerificationRepository = emailVerificationRepository;
    }

    /**
     * 사용자 식별자(userIdx)로 사용자 정보를 조회합니다.
     *
     * @param userIdx 사용자 식별자
     * @return 해당 사용자 정보, 없으면 null
     */
    @Override
    public User findByUserIdx(Long userIdx) {
        return userRepository.findByUserIdx(userIdx).orElse(null);
    }

    /**
     * 로그인 아이디 중복 여부를 확인합니다.
     *
     * @param loginId 확인할 로그인 아이디
     * @return 중복이면 true, 중복이 아니면 false
     */
    @Override
    public boolean isLoginIdDuplicate(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    /**
     * 닉네임 중복 여부를 확인합니다.
     *
     * @param nickname 확인할 닉네임
     * @return 중복이면 true, 중복이 아니면 false
     */
    @Override
    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    /**
     * 로그인 아이디 유효성 검사.
     * 영문 또는 숫자로 이루어진 6~20자만 허용합니다.
     *
     * @param loginId 검사할 로그인 아이디
     * @return 유효하면 true, 아니면 false
     */
    @Override
    public boolean isValidLoginId(String loginId) {
        return Pattern.matches("^[a-zA-Z0-9]{6,20}$", loginId);
    }

    /**
     * 닉네임 유효성 검사.
     * 한글, 영문, 숫자로 이루어진 4~12자만 허용합니다.
     *
     * @param nickname 검사할 닉네임
     * @return 유효하면 true, 아니면 false
     */
    @Override
    public boolean isValidNickname(String nickname) {
        return Pattern.matches("^[가-힣a-zA-Z0-9]{4,12}$", nickname);
    }

    /**
     * 비밀번호 유효성 검사.
     * 영문과 숫자가 반드시 포함되어야 하며,
     * 특수문자는 !@#$%^&*()만 허용되고 길이는 8~20자입니다.
     *
     * @param password 검사할 비밀번호
     * @return 유효하면 true, 아니면 false
     */
    @Override
    public boolean isValidPassword(String password) {
        return Pattern.matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d!@#$%^&*()]{8,20}$", password);
    }

    /**
     * 사용자의 비밀번호를 BCrypt 방식으로 암호화한 후 사용자 정보를 저장합니다.
     * <p>
     * 비밀번호가 null이 아닌 경우에만 암호화를 수행하며, 이후 {@link UserRepository}를 통해 DB에 저장합니다.
     * </p>
     *
     * @param user 저장할 사용자 객체. 비밀번호 필드는 평문이어야 하며, 이 메서드 내에서 암호화됩니다.
     */
    @Override
    public void saveUser(User user) {
        if (user.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
        }
        userRepository.save(user);
    }

    /**
     * 로그인 아이디로 사용자 정보를 조회합니다.
     *
     * @param loginId 로그인 아이디
     * @return 해당 사용자 정보, 없으면 null
     */
    @Override
    public User findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId).orElse(null);
    }
}