package io.github.sunday.devfolio.service.portfolio;

import io.github.sunday.devfolio.dto.portfolio.*;
import io.github.sunday.devfolio.entity.table.portfolio.*;
import io.github.sunday.devfolio.enums.PortfolioSort;
import io.github.sunday.devfolio.dto.user.WriterDto;
import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.repository.portfolio.*;
import io.github.sunday.devfolio.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private final PortfolioImageRepository portfolioImageRepository;
    private final PortfolioLikeRepository portfolioLikeRepository;
    private final PortfolioImageService portfolioImageService;
    private final PortfolioCategoryService portfolioCategoryService;
    private final PortfolioCommentService portfolioCommentService;
    private final UserServiceImpl userService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * 포트폴리오 검색 및 조회
     * 페이지, 키워드, 카테고리, 정렬 기준을 요청을 받음
     */
    // Todo : Index 초과 에러 처리
    public List<PortfolioListDto> search(PortfolioSearchRequestDto searchRequestDto) {
        Pageable pageable = PageRequest.of(searchRequestDto.getPage(), searchRequestDto.getSize());
        List<Portfolio> results = portfolioQueryDslRepository.findAllByKeywordAndCategory(searchRequestDto, pageable);
        return portfoliosToListDto(results);
    }

    /**
     * 최근 핫한 포트폴리오 조회
     * 현재 날짜 기준으로 일주일 간 업로드된 포트폴리오 중 좋아요, 조회수 내림차순으로 상위 5개 선정
     */
    public List<PortfolioListDto> getHotPortfolios() {
        List<Portfolio> results = portfolioRepository.findTop5ByCreatedAtBetweenOrderByLikeCountDescViewsDesc(
                ZonedDateTime.now().minusWeeks(1), ZonedDateTime.now()
        );
        return portfoliosToListDto(results);
    }

    /**
     * 인기 게시글 조회
     * 포트폴리오 중 좋아요, 조회수 내림차순으로 상위 5개 선정
     */
    public List<PortfolioListDto> getPopularPortfolios() {
        List<Portfolio> results = portfolioRepository.findTop5ByOrderByLikeCountDescViewsDescCreatedAtDesc();
        return portfoliosToListDto(results);
    }

    /**
     * 사용자가 좋아요 표시한 포트폴리오 목록 조회
     */
    // Todo : 존재하지 않는 userIdx에 대한 커스텀 에러 처리, Index 초과 에러 처리
    public List<PortfolioLikeListDto> getUserLikedPortfolios(Long userIdx, PortfolioPageRequestDto requestDto) {
        Sort sort = getSortFromPageRequestDto(requestDto, true);
        Pageable pageable = PageRequest.of(requestDto.getPage(), requestDto.getSize(), sort);
        List<PortfolioLike> results = portfolioLikeRepository.findAllByUser_UserIdx(userIdx, pageable);
        return portfoliosToLikeListDto(results);
    }

    /**
     * 사용자의 포트폴리오 목록 조회
     */
    // Todo : Index 초과 에러 처리
    public List<PortfolioListDto> getUserPortfolios(Long userIdx, PortfolioPageRequestDto requestDto) {
        Sort sort = getSortFromPageRequestDto(requestDto, false);
        Pageable pageable = PageRequest.of(requestDto.getPage(), requestDto.getSize(), sort);
        List<Portfolio> results = portfolioRepository.findAllByUser_UserIdx(userIdx, pageable);
        return userPortfoliosToListDto(results);
    }

    /**
     * 포트폴리오 상세 정보 가져오기
     * 포트폴리오 정보, 작성자 정보, 카테고리 정보, 댓글 정보
     */
    public PortfolioDetailDto getPortfolioById(Long portfolioIdx) {
        // 포트폴리오 정보 가져오기
        Portfolio portfolio = portfolioRepository.findById(portfolioIdx).orElseThrow();
        // 포트폴리오 작성자 정보 가져오기
        User user = userService.findByUserIdx(portfolio.getUser().getUserIdx());
        WriterDto writerDto = userToWriterDto(user);

        // Todo : 작성자 없을 때 예외처리 추가
        if (user == null) {
            System.out.println("포트폴리오 작성자 없음");
        }
        // 포트폴리오 카테고리 가져오기
        List<PortfolioCategoryDto> categories = portfolioCategoryService.getCategoriesByPortfolio(portfolio);

        // 포트폴리오 이미지 가져오기
        List<PortfolioImageDto> imageList = portfolioImageService.getPortfolioImages(portfolioIdx);

        // 포트폴리오 댓글 가져오기
        List<PortfolioCommentDto> comments =  portfolioCommentService.getPortfolioComments(portfolioIdx);

        return PortfolioDetailDto.builder()
                .portfolioIdx(portfolioIdx)
                .title(portfolio.getTitle())
                .startDate(portfolio.getStartDate())
                .endDate(portfolio.getEndDate())
                .description(portfolio.getDescription())
                .views(portfolio.getViews())
                .likeCount(portfolio.getLikeCount())
                .commentCount(portfolio.getCommentCount())
                .createdAt(portfolio.getCreatedAt())
                .updatedAt(portfolio.getUpdatedAt())
                .images(imageList)
                .writer(writerDto)
                .categories(categories)
                .comments(comments)
                .build();
    }

    /**
     * 포트폴리오 저장
     * 포트폴리오 데이터, 포트폴리오 카테고리, 썸네일 이미지, 포트폴리오 이미지 저장
     */
    public Long addNewPortfolio(PortfolioWriteRequestDto writeRequestDto, Long userIdx) {
        // 요청을 받는다
        // 사용자 정보를 가져온다
        User user = userService.findByUserIdx(userIdx);
        Portfolio portfolio = writeDtoToPortfolio(writeRequestDto, user);

        // 데이터를 저장한다
        // id값을 반한다
        Portfolio newPortfolio = portfolioRepository.save(portfolio);
        // 포트폴리오 카테고리를 저장한다.
        portfolioCategoryService.addPortfolioCategoryMap(newPortfolio, writeRequestDto.getCategories());

        // 이미지 파일을 저장한다
        // Todo : 에러 핸들링
        try {
            portfolioImageService.addPortfolioImage(portfolio, writeRequestDto);
        } catch (IOException e) {}

        return newPortfolio.getPortfolioIdx();
    }

    // Todo : 포트폴리오 수정 기능 추가


    // Todo : 포트폴리오 삭제 기능 추가

    /**
     * 요청 DTO 정보로 정렬 기준 설정
     */
    private Sort getSortFromPageRequestDto(PortfolioPageRequestDto requestDto, boolean isNestedProperty) {
        // 정렬 기준 설정
        PortfolioSort portfolioSort = requestDto.getSort();
        if (portfolioSort == null) {
            portfolioSort = PortfolioSort.UPDATED_AT;
        }

        // 정렬 형식 변환
        String sortProperty = (isNestedProperty) ?
                "portfolio." + portfolioSort.getFieldName()
                : portfolioSort.getFieldName()
                ;

        // 정렬 방향 설정
        Sort.Direction direction = requestDto.getDirection() != null ? requestDto.getDirection() : Sort.Direction.DESC;

        return Sort.by(direction, sortProperty);
    }

    private WriterDto userToWriterDto(User user) {
        return WriterDto.builder()
                .userIdx(user.getUserIdx())
                .nickname(user.getNickname())
                .profileImg(user.getProfileImg())
                .build();
    }

    private PortfolioListDto portfolioToListDto(Portfolio portfolio, PortfolioImage image, WriterDto writerDto) {
        return PortfolioListDto.builder()
                .portfolioIdx(portfolio.getPortfolioIdx())
                .title(portfolio.getTitle())
                .description(portfolio.getDescription())
                .views(portfolio.getViews())
                .likeCount(portfolio.getLikeCount())
                .updatedAt(portfolio.getUpdatedAt().format(formatter))
                .commentCount(portfolio.getCommentCount())
                .imageUrl(image != null ? image.getImageUrl() : "")
                .writer(writerDto)
                .build();
    }

    private List<PortfolioListDto> portfoliosToListDto(List<Portfolio> portfolios) {
        return portfolios.stream()
                .map(portfolio -> {
                    WriterDto writerDto = userToWriterDto(portfolio.getUser());
                    PortfolioImage image = portfolioImageRepository.findByPortfolio_PortfolioIdxAndIsThumbnailTrue(portfolio.getPortfolioIdx())
                            .orElse(null);
                    return portfolioToListDto(portfolio, image, writerDto);
                }).toList();
    }

    private List<PortfolioListDto> userPortfoliosToListDto(List<Portfolio> portfolios) {
        Portfolio firstPortfolio = portfolios.get(0);
        if (firstPortfolio == null) {
            return new ArrayList<>();
        }
        WriterDto writerDto = userToWriterDto(firstPortfolio.getUser());

        return portfolios.stream()
                .map(portfolio -> {
                    PortfolioImage image = portfolioImageRepository.findByPortfolio_PortfolioIdxAndIsThumbnailTrue(portfolio.getPortfolioIdx())
                            .orElse(null);
                    return portfolioToListDto(portfolio, image, writerDto);
                }).toList();
    }

    private List<PortfolioLikeListDto> portfoliosToLikeListDto(List<PortfolioLike> portfolioLikes) {
        return portfolioLikes.stream()
                .map(portfolioLike -> {
                    Portfolio portfolio = portfolioLike.getPortfolio();
                    WriterDto writerDto = userToWriterDto(portfolio.getUser());
                    PortfolioImage image = portfolioImageRepository.findByPortfolio_PortfolioIdxAndIsThumbnailTrue(portfolio.getPortfolioIdx())
                            .orElse(null);

                    return PortfolioLikeListDto.builder()
                            .portfolioListDto(portfolioToListDto(portfolio, image, writerDto))
                            .likedAt(portfolioLike.getLikedAt().format(formatter))
                            .build();
                }).toList();
    }

    private Portfolio writeDtoToPortfolio(PortfolioWriteRequestDto writeRequestDto, User user) {
        return Portfolio.builder()
                .title(writeRequestDto.getTitle())
                .startDate(writeRequestDto.getStartDate())
                .endDate(writeRequestDto.getEndDate())
                .description(writeRequestDto.getDescription())
                .views(0)
                .likeCount(0)
                .commentCount(0)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .user(user)
                .build();
    }
}
