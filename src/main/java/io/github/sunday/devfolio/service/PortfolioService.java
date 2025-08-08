package io.github.sunday.devfolio.service;

import io.github.sunday.devfolio.repository.portfolio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final PortfolioCategoryRepository portfolioCategoryRepository;
    private final PortfolioCategoryMapRepository portfolioCategoryMapRepository;
    private final PortfolioCommentRepository portfolioCommentRepository;
    private final PortfolioImageRepository portfolioImageRepository;
    private final PortfolioLikeRepository portfolioLikeRepository;
    // Todo : UserRepository 추가


    // Todo : 포트폴리오 검색 기능 추가


    // Todo : 포트폴리오 상세보기 기능 추가


    // Todo : 포트폴리오 작성 기능 추가


    // Todo : 포트폴리오 수정 기능 추가


    // Todo : 포트폴리오 삭제 기능 추가
}
