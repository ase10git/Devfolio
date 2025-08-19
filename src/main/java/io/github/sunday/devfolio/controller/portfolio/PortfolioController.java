package io.github.sunday.devfolio.controller.portfolio;

import io.github.sunday.devfolio.dto.portfolio.*;
import io.github.sunday.devfolio.enums.PortfolioSort;
import io.github.sunday.devfolio.exception.portfolio.NoWriterFoundException;
import io.github.sunday.devfolio.exception.portfolio.PortfolioNotFoundException;
import io.github.sunday.devfolio.service.portfolio.PortfolioCategoryService;
import io.github.sunday.devfolio.service.portfolio.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * 포트폴리오 요청을 처리하는 Controller
 */
@Controller
@RequestMapping("/portfolio")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final PortfolioCategoryService portfolioCategoryService;

    /**
     * 포트폴리오 검색 요청이 들어올 때 DTO 내의 String에서 script를 제거
     */
    @InitBinder("searchRequestDto")
    public void initBinder(WebDataBinder binder) {
        // keyword와 category의 sanitize 수행
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
     * 포트폴리오 작성 요청이 들어올 때 DTO 내의 String에서 script를 제거
     */
    @InitBinder({"writeRequestDto", "editRequestDto"})
    public void initBinderToWrite(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true) {
            @Override
            public void setAsText(String text) {
                if (text != null) {
                    String safeText = Jsoup.clean(text, Safelist.relaxed());
                    super.setAsText(safeText.trim());
                } else {
                    super.setValue(null);
                }
            }
        });
    }

    /**
     * 포트폴리오 메인 페이지 출력
     * 포트폴리오 검색, 핫한 포트폴리오 제공
     */
    @GetMapping()
    public String list(
            @Valid @ModelAttribute("searchRequestDto") PortfolioSearchRequestDto requestDto,
            BindingResult bindingResult,
            Model model
    ) {
        List<String> errorMessages = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();
        }

        List<PortfolioListDto> list = portfolioService.search(requestDto);

        boolean isHot = true;
        List<PortfolioListDto> hotList = portfolioService.getHotPortfolios();
        if (hotList.isEmpty()) {
            hotList = portfolioService.getPopularPortfolios();
            isHot = false;
        }
        List<PortfolioCategoryDto> categoryList = portfolioCategoryService.getCachedCategories();

        model.addAttribute("hotPortfolios", hotList);
        model.addAttribute("isHot", isHot);
        model.addAttribute("portfolios", list);
        model.addAttribute("categories", categoryList);
        model.addAttribute("requestDto", requestDto);
        model.addAttribute("sortOptions", PortfolioSort.values());

        if (!errorMessages.isEmpty()) {
            model.addAttribute("error", errorMessages);
        }
        return "portfolio/portfolio";
    }

    /**
     * 포트폴리오 상세 페이지 출력
     */
    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            Model model
    ) {
        // Todo : 전역 에러 처리 필요
        try {
            PortfolioDetailDto detailDto = portfolioService.getPortfolioById(id);
            model.addAttribute("portfolio", detailDto);
            return "portfolio/portfolio_detail";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    /**
     * 포트폴리오 작성 페이지 출력
     */
    @GetMapping("/new")
    public String writePage(
            @ModelAttribute PortfolioWriteRequestDto writeDto,
            Model model
    ) {
        List<PortfolioCategoryDto> categoryList = portfolioCategoryService.getCachedCategories();
        model.addAttribute("writeDto", writeDto);
        model.addAttribute("categories", categoryList);
        return "portfolio/portfolio_write";
    }

    /**
     * 포트폴리오 작성 동작
     */
    // Todo : 전역 에러 처리 설정 필요
    @PostMapping("/new")
    public String write(
            @ModelAttribute("writeRequestDto") PortfolioWriteRequestDto writeDto,
            BindingResult bindingResult,
            Model model
            ) {

        if (bindingResult.hasErrors()) {
            List<PortfolioCategoryDto> categories = portfolioCategoryService.getCachedCategories();
            model.addAttribute("writeDto", writeDto);
            model.addAttribute("categories", categories);
            return "portfolio/portfolio_write";
        }
        // Todo : 로그인한 사용자 정보 전달
        Long testUserIdx = 1L;
        Long portfolioIdx = portfolioService.addNewPortfolio(writeDto, testUserIdx);
        return "redirect:/portfolio_detail/" + portfolioIdx;
    }

    /**
     * 포트폴리오 수정 페이지 출력
     */
    @GetMapping("/{id}/edit")
    public String editPage(
            @PathVariable Long id,
            Model model
    ) {
        return "portfolio/portfolio_edit";
    }

    /**
     * 포트폴리오 수정
     */
    @PostMapping("/{id}/edit")
    public String edit(
            @PathVariable Long id,
            Model model
    ) {
        return "portfolio/portfolio_edit";
    }
}
