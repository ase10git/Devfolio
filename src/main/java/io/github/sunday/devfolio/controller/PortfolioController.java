package io.github.sunday.devfolio.controller;

import io.github.sunday.devfolio.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/portfolio")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;

    // Todo : 포트폴리오 검색 기능 추가
    @GetMapping
    public String list() {
        return "portfolio/portfolio";
    }

    // Todo : 포트폴리오 상세보기 기능 추가
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id) {
        return "portfolio/portfolio_detail";
    }

    // Todo : 포트폴리오 작성 기능 추가
    @GetMapping("/new")
    public String write() {
        return "portfolio/portfolio_write";
    }

    // Todo : 포트폴리오 수정 기능 추가
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id) {
        return "portfolio/portfolio_edit";
    }
    
    // Todo : 포트폴리오 삭제 기능 추가
}
