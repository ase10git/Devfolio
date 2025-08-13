package io.github.sunday.devfolio.controller;

import io.github.sunday.devfolio.dto.portfolio.PortfolioCategoryDto;
import io.github.sunday.devfolio.dto.portfolio.PortfolioListDto;
import io.github.sunday.devfolio.dto.portfolio.PortfolioSearchRequestDto;
import io.github.sunday.devfolio.dto.portfolio.PortfolioSort;
import io.github.sunday.devfolio.service.portfolio.PortfolioCategoryService;
import io.github.sunday.devfolio.service.portfolio.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/portfolio")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final PortfolioCategoryService portfolioCategoryService;

    @GetMapping
    public String list(
            @ModelAttribute PortfolioSearchRequestDto requestDto,
            Model model
    ) {
        List<PortfolioListDto> list = portfolioService.search(requestDto);

        boolean isHot = true;
        List<PortfolioListDto> hotList = portfolioService.getHotPortfolios();
        if (hotList.isEmpty()) {
            hotList = portfolioService.getPopularPortfolios();
            isHot = false;
        }
        List<PortfolioCategoryDto> categoryList = portfolioCategoryService.list();

        model.addAttribute("hotPortfolios", hotList);
        model.addAttribute("isHot", isHot);
        model.addAttribute("portfolios", list);
        model.addAttribute("categories", categoryList);
        model.addAttribute("requestDto", requestDto);
        model.addAttribute("sortOptions", PortfolioSort.values());
        return "portfolio/portfolio";
    }

    @GetMapping("/api/list")
    public ResponseEntity<?> apiList(
            @ModelAttribute PortfolioSearchRequestDto requestDto
    ) {
        List<PortfolioListDto> list = portfolioService.search(requestDto);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id) {
        return "portfolio/portfolio_detail";
    }

    @GetMapping("/new")
    public String write() {
        return "portfolio/portfolio_write";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id) {
        return "portfolio/portfolio_edit";
    }
}
