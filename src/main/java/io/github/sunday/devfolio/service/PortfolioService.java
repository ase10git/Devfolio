package io.github.sunday.devfolio.service;

import io.github.sunday.devfolio.dto.portfolio.PortfolioCategoryDto;
import io.github.sunday.devfolio.dto.portfolio.PortfolioListDto;
import io.github.sunday.devfolio.dto.portfolio.PortfolioSearchRequestDto;
import io.github.sunday.devfolio.dto.user.WriterDto;
import io.github.sunday.devfolio.entity.table.portfolio.Portfolio;
import io.github.sunday.devfolio.entity.table.portfolio.PortfolioCategoryMap;
import io.github.sunday.devfolio.entity.table.portfolio.PortfolioImage;
import io.github.sunday.devfolio.repository.portfolio.*;
import lombok.RequiredArgsConstructor;
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
    private final PortfolioQueryDslRepository portfolioQueryDslRepository;
    private final PortfolioCategoryRepository portfolioCategoryRepository;
    private final PortfolioCategoryMapRepository portfolioCategoryMapRepository;
    private final PortfolioCommentRepository portfolioCommentRepository;
    private final PortfolioImageRepository portfolioImageRepository;
    private final PortfolioLikeRepository portfolioLikeRepository;
    private static final int LIST_PAGE_SIZE = 20;

    /**
     * 포트폴리오 검색 및 조회
     * 페이지, 키워드, 카테고리, 정렬 기준을 요청을 받음
     */
    // Todo : 포트폴리오 검색 기능 추가
    public List<PortfolioListDto> search(PortfolioSearchRequestDto searchRequestDto) {
        Pageable pageable = PageRequest.of(searchRequestDto.getPage(), LIST_PAGE_SIZE);

        List<Portfolio> results = portfolioQueryDslRepository.findAllByKeywordAndCategory(searchRequestDto, pageable);

        return results.stream()
                .map(portfolio -> {
                    WriterDto writerDto = WriterDto.builder()
                            .userIdx(portfolio.getUser().getUserIdx())
                            .nickname(portfolio.getUser().getNickname())
                            .profileImg(portfolio.getUser().getProfileImg())
                            .build();

                    PortfolioImage image = portfolioImageRepository.findFirst1ByPortfolio(portfolio).orElse(null);
                    List<PortfolioCategoryMap> categoryMapList = portfolioCategoryMapRepository.findAllByPortfolio(portfolio);
                    List<PortfolioCategoryDto> categoryDtoList = categoryMapList.stream()
                            .map(categoryMap ->  PortfolioCategoryDto.builder()
                                        .categoryIdx(categoryMap.getCategory().getCategoryIdx())
                                        .name(categoryMap.getCategory().getName())
                                        .build()
                            )
                            .toList();

                    return PortfolioListDto.builder()
                            .portfolioIdx(portfolio.getPortfolioIdx())
                            .title(portfolio.getTitle())
                            .description(portfolio.getDescription())
                            .views(portfolio.getViews())
                            .likeCount(portfolio.getLikeCount())
                            .updatedAt(portfolio.getUpdatedAt())
                            .commentCount(portfolio.getCommentCount())
                            .imageUrl(image != null ? image.getImageUrl() : "")
                            .writer(writerDto)
                            .categories(categoryDtoList)
                            .build();
        }).toList();
    }

    // Todo : 포트폴리오 상세보기 기능 추가


    // Todo : 포트폴리오 작성 기능 추가


    // Todo : 포트폴리오 수정 기능 추가


    // Todo : 포트폴리오 삭제 기능 추가
}
