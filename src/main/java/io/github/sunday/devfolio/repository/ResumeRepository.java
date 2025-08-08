package io.github.sunday.devfolio.repository;

import io.github.sunday.devfolio.entity.table.profile.Resume;
import io.github.sunday.devfolio.entity.table.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    /** 특정 사용자의 모든 이력서 조회 */
    //List<Resume> findAllByUser(User user);
}