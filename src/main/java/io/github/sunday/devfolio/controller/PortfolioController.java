package io.github.sunday.devfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/portfolio")
public class PortfolioController {

    @GetMapping
    public String list() {
        return "portfolio/portfolio";
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
