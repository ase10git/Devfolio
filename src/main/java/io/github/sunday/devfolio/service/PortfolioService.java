package io.github.sunday.devfolio.service;

import io.github.sunday.devfolio.dto.portfolio.PortfolioListDto;
import io.github.sunday.devfolio.dto.portfolio.PortfolioSearchRequestDto;
import io.github.sunday.devfolio.dto.user.WriterDto;
import io.github.sunday.devfolio.entity.table.portfolio.Portfolio;
import io.github.sunday.devfolio.repository.portfolio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 포트폴리오 Entity를 다루는 Service
 */
@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final PortfolioCategoryRepository portfolioCategoryRepository;
    private final PortfolioCategoryMapRepository portfolioCategoryMapRepository;
    private final PortfolioCommentRepository portfolioCommentRepository;
    private final PortfolioImageRepository portfolioImageRepository;
    private final PortfolioLikeRepository portfolioLikeRepository;
    private static final int LIST_PAGE_SIZE = 20;

    // Todo : 포트폴리오 검색 기능 추가
    public List<PortfolioListDto> search(PortfolioSearchRequestDto searchRequestDto) {
        // 페이지 생성
        Pageable pageable = PageRequest.of(searchRequestDto.getPage(), LIST_PAGE_SIZE);

        // repository에서 portfolio 가져오기
        Page<Portfolio> pages = portfolioRepository.findAll(pageable);

        // 카테고리 검색 결과

        // 키워드 검색 결과

        // 정렬 수행

        // 리스트로 가공
        return pages.stream()
                .map(portfolio -> {
                    WriterDto writerDto = WriterDto.builder()
                            .userIdx(portfolio.getUser().getUserIdx())
                            .nickname(portfolio.getUser().getNickname())
                            .profileImg(portfolio.getUser().getProfileImg())
                            .build();

                    return PortfolioListDto.builder()
                            .portfolioIdx(portfolio.getPortfolioIdx())
                            .title(portfolio.getTitle())
                            .description(portfolio.getDescription())
                            .views(portfolio.getViews())
                            .likeCount(portfolio.getLikeCount())
                            .updatedAt(portfolio.getUpdatedAt())
                            .commentCount(0)
                            .imageUrl("")
                            .writer(writerDto)
                            .build();
        }).toList();
    }

    // Todo : 포트폴리오 상세보기 기능 추가


    // Todo : 포트폴리오 작성 기능 추가


    // Todo : 포트폴리오 수정 기능 추가


    // Todo : 포트폴리오 삭제 기능 추가
}
