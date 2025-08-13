package io.github.sunday.devfolio.controller.portfolio;

import io.github.sunday.devfolio.dto.portfolio.PortfolioListDto;
import io.github.sunday.devfolio.dto.portfolio.PortfolioSearchRequestDto;
import io.github.sunday.devfolio.service.portfolio.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 포트폴리오 요청을 처리하는 RestController
 */
@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioRestController {
    private final PortfolioService portfolioService;

    /**
     * 포트폴리오 목록 출력 API
     * 무한 스크롤에 적용되는 정보 요청 API
     */
    @GetMapping("/list")
    public ResponseEntity<List<PortfolioListDto>> list(
            @ModelAttribute PortfolioSearchRequestDto requestDto
    ) {
        List<PortfolioListDto> list = portfolioService.search(requestDto);
        return ResponseEntity.ok().body(list);
    }

//    // Todo : UserController로 이동 예정
//    // 추천 형식 : /api/user/{id}/portfolios
//    @GetMapping("/users/{userIdx}")
//    public ResponseEntity<List<PortfolioListDto>> userList(
//            @PathVariable Long userIdx,
//            @ModelAttribute PageRequestDto requestDto
//            ) {
//        List<PortfolioListDto> list = portfolioService.getUserPortfolios(userIdx, requestDto);
//        return ResponseEntity.ok().body(list);
//    }
//
//    // Todo : UserController로 이동 예정
//    // 추천 형식 : /api/user/{id}/liked-portfolios
//    @GetMapping("/users/{userIdx}/liked-portfolios")
//    public ResponseEntity<List<PortfolioLikeListDto>> userLikedList(
//            @PathVariable Long userIdx,
//            @ModelAttribute PageRequestDto requestDto
//    ) {
//        List<PortfolioLikeListDto> list = portfolioService.getUserLikedPortfolios(userIdx, requestDto);
//        return ResponseEntity.ok().body(list);
//    }
}
