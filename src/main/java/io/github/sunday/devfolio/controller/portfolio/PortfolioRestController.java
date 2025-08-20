package io.github.sunday.devfolio.controller.portfolio;

import io.github.sunday.devfolio.dto.portfolio.PortfolioListDto;
import io.github.sunday.devfolio.dto.portfolio.PortfolioSearchRequestDto;
import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.enums.PortfolioSort;
import io.github.sunday.devfolio.service.portfolio.PortfolioLikeService;
import io.github.sunday.devfolio.service.portfolio.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 포트폴리오 요청을 처리하는 RestController
 */
@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioRestController {
    private final PortfolioService portfolioService;
    private final PortfolioLikeService portfolioLikeService;

    /**
     * 포트폴리오 검색 요청이 들어올 때 DTO 내의 String에서 script를 제거
     */
    @InitBinder("requestDto")
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true) {
            @Override
            public void setAsText(String text) {
                if (text != null) {
                    String safeText = Jsoup.clean(text, Safelist.basic());
                    super.setAsText(safeText.trim());
                } else {
                    super.setValue(null);
                }
            }
        });

        // sort 유효성 검증
        binder.registerCustomEditor(PortfolioSort.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                PortfolioSort sort = PortfolioSort.fromFieldName(text);
                if (sort == null) {
                    sort = PortfolioSort.UPDATED_AT;
                }
                setValue(sort);
            }
        });
    }

    /**
     * 포트폴리오 목록 출력 API
     * 무한 스크롤에 적용되는 정보 요청 API
     */
    @GetMapping("/list")
    public ResponseEntity<?> list(
            @Valid @ModelAttribute("requestDto") PortfolioSearchRequestDto requestDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            Map<String, String> fieldErrors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return ResponseEntity.badRequest().body(fieldErrors);
        }

        List<PortfolioListDto> list = portfolioService.search(requestDto);
        return ResponseEntity.ok().body(list);
    }


    /**
     * 포트폴리오 제거
     */
    @PostMapping("/{id}/delete")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        Map<String, Object> responseData = new HashMap<>();
        try {
            // Todo : 로그인 필요 예외처리
            if (user == null) throw new Exception("로그인이 필요합니다");
            Long userIdx = user.getUserIdx();
            portfolioService.deletePortfolio(id, userIdx);

            responseData.put("message", "성공적으로 제거했습니다.");
            return ResponseEntity.ok().body(responseData);
        } catch (Exception e) {
            responseData.put("message", "포트폴리오 제거에 실패했습니다.");
            return ResponseEntity.internalServerError().body(responseData);
        }
    }

    /**
     * 포트폴리오 좋아요 추가
     */
    @PostMapping("/{id}/add-like")
    public ResponseEntity<?> addLike(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        Map<String, Object> responseData = new HashMap<>();
        try {
            // Todo : 로그인 필요 예외처리
            // Todo : 로그인한 사용자 정보 전달
            //Long userIdx = user.getUserIdx();
            Long testUserIdx = 2L;
            portfolioLikeService.addLike(id, testUserIdx);

            responseData.put("message", "성공적으로 추가했습니다");
            return ResponseEntity.ok().body(responseData);
        } catch (Exception e) {
            responseData.put("message", "좋아요 등록에 실패했습니다.");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(responseData);
        }
    }

    /**
     * 포트폴리오 좋아요 제거
     */
    @PostMapping("/{id}/remove-like")
    public ResponseEntity<?> removeLike(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        Map<String, Object> responseData = new HashMap<>();
        try {
            // Todo : 로그인 필요 예외처리
            // Todo : 로그인한 사용자 정보 전달
            //Long userIdx = user.getUserIdx();
            Long testUserIdx = 2L;
            portfolioLikeService.removeLike(id, testUserIdx);

            responseData.put("message", "성공적으로 제거했습니다");
            return ResponseEntity.ok().body(responseData);
        } catch (Exception e) {
            responseData.put("message", "좋아요 제거에 실패했습니다.");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(responseData);
        }
    }
}
