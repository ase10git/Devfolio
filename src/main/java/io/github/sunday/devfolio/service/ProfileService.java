package io.github.sunday.devfolio.service;

import io.github.sunday.devfolio.DTO.ProfileDto;
import io.github.sunday.devfolio.model.Follow;
import io.github.sunday.devfolio.model.Resume;
import io.github.sunday.devfolio.model.User;
import io.github.sunday.devfolio.repository.FollowRepository;
import io.github.sunday.devfolio.repository.ResumeRepository;
import io.github.sunday.devfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepo;
    private final FollowRepository followrepo;
    private final ResumeRepository
}
