package io.github.sunday.devfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 기본 경로와 메인 페이지 경로를 관리하는 컨트롤러
 */
@Controller
public class MainController {

    /**
     * 기본 경로 설정
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/main";
    }

    /**
     * 메인 페이지로 이동
     */
    @GetMapping("/main")
    public String mainPage() {
        return "main";
    }
}