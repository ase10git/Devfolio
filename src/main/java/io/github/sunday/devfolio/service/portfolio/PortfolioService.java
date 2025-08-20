package io.github.sunday.devfolio.service.portfolio;

import io.github.sunday.devfolio.dto.portfolio.*;
import io.github.sunday.devfolio.entity.table.portfolio.*;
import io.github.sunday.devfolio.enums.PortfolioSort;
import io.github.sunday.devfolio.dto.user.WriterDto;
import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.exception.portfolio.NoWriterFoundException;
import io.github.sunday.devfolio.exception.portfolio.PortfolioNotFoundException;
import io.github.sunday.devfolio.repository.portfolio.*;
import io.github.sunday.devfolio.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private final DateTimeFormatter dateTimeformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
    public PortfolioDetailDto getPortfolioById(Long portfolioIdx) throws Exception {
        // 포트폴리오 정보 가져오기
        Portfolio portfolio = portfolioRepository.findById(portfolioIdx).orElseThrow(
                () -> new PortfolioNotFoundException("포트폴리오가 존재하지 않습니다.")
        );

        // 포트폴리오 작성자 정보 가져오기
        User user = userService.findByUserIdx(portfolio.getUser().getUserIdx());
        if (user == null) {
            throw new NoWriterFoundException("작성자가 존재하지 않습니다");
        }
        WriterDto writerDto = userToWriterDto(user);

        // 포트폴리오 카테고리 가져오기
        List<PortfolioCategoryDto> categories = portfolioCategoryService.getCategoriesByPortfolio(portfolio);

        // 포트폴리오 이미지 가져오기
        PortfolioImage thumbnail = portfolioImageService.getPortfolioThumbnail(portfolioIdx);
        String thumbnailUrl = thumbnail != null ? thumbnail.getImageUrl() : null;

        // 포트폴리오 댓글 가져오기
        List<PortfolioCommentDto> comments =  portfolioCommentService.getPortfolioComments(portfolioIdx);

        String startDate = portfolio.getStartDate() != null ? portfolio.getStartDate().format(dateFormatter) : null;
        String endDate = portfolio.getEndDate() != null ? portfolio.getEndDate().format(dateFormatter) : null;
        String createdAt = portfolio.getCreatedAt() != null ? portfolio.getCreatedAt().format(dateTimeformatter) : null;
        String updatedAt = portfolio.getUpdatedAt() != null ? portfolio.getUpdatedAt().format(dateTimeformatter) : null;

        return PortfolioDetailDto.builder()
                .portfolioIdx(portfolioIdx)
                .title(portfolio.getTitle())
                .startDate(startDate)
                .endDate(endDate)
                .description(portfolio.getDescription())
                .views(portfolio.getViews())
                .likeCount(portfolio.getLikeCount())
                .commentCount(portfolio.getCommentCount())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .thumbnailUrl(thumbnailUrl)
                .writer(writerDto)
                .categories(categories)
                .comments(comments)
                .build();
    }

    /**
     * 포트폴리오 수정용 DTO 생성하기
     */
    public PortfolioEditRequestDto buildEditDto(Long portfolioIdx) throws Exception {
        // 포트폴리오 정보 가져오기
        Portfolio portfolio = portfolioRepository.findById(portfolioIdx).orElseThrow(
                () -> new PortfolioNotFoundException("포트폴리오가 존재하지 않습니다.")
        );

        // 포트폴리오 작성자 정보 가져오기
        User user = userService.findByUserIdx(portfolio.getUser().getUserIdx());
        if (user == null) {
            throw new NoWriterFoundException("작성자가 존재하지 않습니다");
        }

        // 포트폴리오 카테고리 가져오기
        List<PortfolioCategoryDto> categories = portfolioCategoryService.getCategoriesByPortfolio(portfolio);
        List<Long> categoryIdxList = categories.stream().map(category -> category.getCategoryIdx()).toList();

        // 포트폴리오 이미지 가져오기
        List<PortfolioImage> images = portfolioImageService.getPortfolioImages(portfolioIdx);
        PortfolioImage thumbnail = images.stream()
                .filter(image -> image.getIsThumbnail()).findAny().orElse(null);
        String thumbnailUrl = "";
        if (thumbnail != null) thumbnailUrl = thumbnail.getImageUrl();

        List<String> imageUrlList = images.stream()
                .filter(image -> !image.getIsThumbnail())
                .map(image->image.getImageUrl())
                .toList();

        return PortfolioEditRequestDto.builder()
                .portfolioIdx(portfolioIdx)
                .title(portfolio.getTitle())
                .startDate(portfolio.getStartDate())
                .endDate(portfolio.getEndDate())
                .description(portfolio.getDescription())
                .categories(categoryIdxList)
                .images(imageUrlList)
                .thumbnailUrl(thumbnailUrl)
                .build();
    }

    /**
     * 포트폴리오 저장
     * 포트폴리오 데이터, 포트폴리오 카테고리, 썸네일 이미지, 포트폴리오 이미지 저장
     */
    public Long addNewPortfolio(PortfolioWriteRequestDto writeRequestDto, Long userIdx) {
        // 사용자 검색
        User user = userService.findByUserIdx(userIdx);
        // Todo : 사용자 없을 때의 에러 처리
        if (user == null) {
            return null;
        }

        Portfolio portfolio = writeDtoToPortfolio(writeRequestDto, user);

        // 포트폴리오 데이터 저장
        Portfolio newPortfolio = portfolioRepository.save(portfolio);
        // 포트폴리오 카테고리를 저장
        portfolioCategoryService.addPortfolioCategoryMap(newPortfolio, writeRequestDto.getCategories());

        // 이미지 파일 저장
        // Todo : 에러 핸들링
        try {
            portfolioImageService.addPortfolioImage(portfolio, writeRequestDto, userIdx);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newPortfolio.getPortfolioIdx();
    }

    /**
     * 포트폴리오 수정
     */
    public Long editPortfolio(PortfolioEditRequestDto editRequestDto, Long portfolioIdx, Long userIdx) throws Exception {
        try {
        // 사용자 검색
        User user = userService.findByUserIdx(userIdx);

        // Todo : 사용자 없을 때의 에러 처리
        if (user == null) {
            return null;
        }
        // Todo : 포트폴리오가 없을 때 에러 처리
        Portfolio original = portfolioRepository.findById(portfolioIdx).orElse(null);
        if (original == null) {
            return null;
        }
        
        // Todo : 포트폴리오 idx와 dto내의 idx가 다를 때 에러 처리
            System.out.println(editRequestDto.getPortfolioIdx());
            System.out.println(portfolioIdx);
        if (!portfolioIdx.equals(editRequestDto.getPortfolioIdx())) {
            return null;
        }
        
        // 수정 DTO를 portfolio에 반영
        Portfolio portfolio = editDtoToPortfolio(editRequestDto, original);

        // 포트폴리오 데이터 저장
        Portfolio edittedPortfolio = portfolioRepository.save(portfolio);

        // 포트폴리오 카테고리를 수정
        portfolioCategoryService.editPortfolioCategoryMap(edittedPortfolio, editRequestDto.getCategories());

        // 이미지 파일 저장
        // Todo : 에러 핸들링

            portfolioImageService.editPortfolioImage(edittedPortfolio, editRequestDto, userIdx);
            return portfolio.getPortfolioIdx();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 포트폴리오 삭제
     */
    public void deletePortfolio(Long portfolioIdx, Long userIdx) throws Exception {
        // 사용자 검증
        User user = userService.findByUserIdx(userIdx);

        // Todo : 사용자 없을 때의 에러 처리
        if (user == null) {
            throw new Exception("사용자가 존재하지 않습니다");
        }

        // 포트폴리오 검색
        Portfolio portfolio = portfolioRepository.findById(portfolioIdx).orElse(null);
        if (portfolio == null) return;

        // 포트폴리오 주인과 사용자 비교
        if (!user.getUserIdx().equals(portfolio.getUser().getUserIdx())) {
            throw new Exception("접근이 차단되었습니다");
        }

        try {
            // S3 이미지 제거
            portfolioImageService.deleteImages(portfolioIdx);

            // 포트폴리오 제거
            portfolioRepository.deleteById(portfolioIdx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private PortfolioListDto portfolioToListDto(Portfolio portfolio, PortfolioImage image, WriterDto writerDto, List<PortfolioCategoryDto> categories) {
        return PortfolioListDto.builder()
                .portfolioIdx(portfolio.getPortfolioIdx())
                .title(portfolio.getTitle())
                .views(portfolio.getViews())
                .likeCount(portfolio.getLikeCount())
                .updatedAt(portfolio.getUpdatedAt().format(dateTimeformatter))
                .commentCount(portfolio.getCommentCount())
                .imageUrl(image != null ? image.getImageUrl() : "")
                .writer(writerDto)
                .categories(categories)
                .build();
    }

    private List<PortfolioListDto> portfoliosToListDto(List<Portfolio> portfolios) {
        return portfolios.stream()
                .map(portfolio -> {
                    WriterDto writerDto = userToWriterDto(portfolio.getUser());
                    PortfolioImage image = portfolioImageRepository.findByPortfolio_PortfolioIdxAndIsThumbnailTrue(portfolio.getPortfolioIdx())
                            .orElse(null);
                    List<PortfolioCategoryDto> categories = portfolioCategoryService.getCategoriesByPortfolio(portfolio);
                    return portfolioToListDto(portfolio, image, writerDto, categories);
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
                    List<PortfolioCategoryDto> categories = portfolioCategoryService.getCategoriesByPortfolio(portfolio);
                    return portfolioToListDto(portfolio, image, writerDto, categories);
                }).toList();
    }

    private List<PortfolioLikeListDto> portfoliosToLikeListDto(List<PortfolioLike> portfolioLikes) {
        return portfolioLikes.stream()
                .map(portfolioLike -> {
                    Portfolio portfolio = portfolioLike.getPortfolio();
                    WriterDto writerDto = userToWriterDto(portfolio.getUser());
                    PortfolioImage image = portfolioImageRepository.findByPortfolio_PortfolioIdxAndIsThumbnailTrue(portfolio.getPortfolioIdx())
                            .orElse(null);
                    List<PortfolioCategoryDto> categories = portfolioCategoryService.getCategoriesByPortfolio(portfolio);
                    return PortfolioLikeListDto.builder()
                            .portfolioListDto(portfolioToListDto(portfolio, image, writerDto, categories))
                            .likedAt(portfolioLike.getLikedAt().format(dateTimeformatter))
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

    private Portfolio editDtoToPortfolio(PortfolioEditRequestDto editRequestDto, Portfolio portfolio) {
        portfolio.setTitle(editRequestDto.getTitle());
        portfolio.setStartDate(editRequestDto.getStartDate());
        portfolio.setEndDate(editRequestDto.getEndDate());
        portfolio.setDescription(editRequestDto.getDescription());
        portfolio.setUpdatedAt(ZonedDateTime.now());

        return portfolio;
    }
}
