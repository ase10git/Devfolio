package io.github.sunday.devfolio.service.portfolio;

import io.github.sunday.devfolio.dto.portfolio.PortfolioListDto;
import io.github.sunday.devfolio.dto.portfolio.PortfolioSearchRequestDto;
import io.github.sunday.devfolio.dto.user.WriterDto;
import io.github.sunday.devfolio.entity.table.portfolio.Portfolio;
import io.github.sunday.devfolio.entity.table.portfolio.PortfolioImage;
import io.github.sunday.devfolio.repository.portfolio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 포트폴리오 Entity를 다루는 Service
 * 포트폴리오 CRUD 기능 제공
 */
@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final PortfolioQueryDslRepository portfolioQueryDslRepository;
    private final PortfolioCategoryMapRepository portfolioCategoryMapRepository;
    private final PortfolioCommentRepository portfolioCommentRepository;
    private final PortfolioImageRepository portfolioImageRepository;
    private final PortfolioLikeRepository portfolioLikeRepository;
    private static final int LIST_PAGE_SIZE = 20;

    /**
     * 포트폴리오 검색 및 조회
     * 페이지, 키워드, 카테고리, 정렬 기준을 요청을 받음
     */
    public List<PortfolioListDto> search(PortfolioSearchRequestDto searchRequestDto) {
        Pageable pageable = PageRequest.of(searchRequestDto.getPage(), LIST_PAGE_SIZE);

        List<Portfolio> results = portfolioQueryDslRepository.findAllByKeywordAndCategory(searchRequestDto, pageable);

        return portfolioToListDto(results);
    }

    /**
     * 최근 핫한 포트폴리오 조회
     * 현재 날짜 기준으로 일주일 간 업로드된 포트폴리오 중 좋아요, 조회수 내림차순으로 상위 5개 선정
     */
    public List<PortfolioListDto> getHotPortfolios() {
        List<Portfolio> results = portfolioRepository.findTop5ByCreatedAtBetweenOrderByLikeCountDescViewsDesc(
                ZonedDateTime.now().minusWeeks(1), ZonedDateTime.now()
        );
        return portfolioToListDto(results);
    }

    /**
     * 인기 게시글 조회
     * 포트폴리오 중 좋아요, 조회수 내림차순으로 상위 5개 선정
     */
    public List<PortfolioListDto> getPopularPortfolios() {
        List<Portfolio> results = portfolioRepository.findTop5ByOrderByLikeCountDescViewsDescCreatedAtDesc();
        return portfolioToListDto(results);
    }

    private List<PortfolioListDto> portfolioToListDto(List<Portfolio> portfolios) {
        return portfolios.stream()
                .map(portfolio -> {
                    WriterDto writerDto = WriterDto.builder()
                            .userIdx(portfolio.getUser().getUserIdx())
                            .nickname(portfolio.getUser().getNickname())
                            .profileImg(portfolio.getUser().getProfileImg())
                            .build();

                    PortfolioImage image = portfolioImageRepository.findFirst1ByPortfolio(portfolio).orElse(null);

                    return PortfolioListDto.builder()
                            .portfolioIdx(portfolio.getPortfolioIdx())
                            .title(portfolio.getTitle())
                            .description(portfolio.getDescription())
                            .views(portfolio.getViews())
                            .likeCount(portfolio.getLikeCount())
                            .updatedAt(
                                    portfolio.getUpdatedAt().format(
                                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                                    )
                            )
                            .commentCount(portfolio.getCommentCount())
                            .imageUrl(image != null ? image.getImageUrl() : "")
                            .writer(writerDto)
                            .build();
                }).toList();
    }

    // Todo : 포트폴리오 상세보기 기능 추가


    // Todo : 포트폴리오 작성 기능 추가


    // Todo : 포트폴리오 수정 기능 추가


    // Todo : 포트폴리오 삭제 기능 추가
}
