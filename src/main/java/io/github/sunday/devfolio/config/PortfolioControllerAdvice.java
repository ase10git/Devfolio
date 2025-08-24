package io.github.sunday.devfolio.config;

import io.github.sunday.devfolio.controller.portfolio.PortfolioController;
import io.github.sunday.devfolio.controller.portfolio.PortfolioRestController;
import io.github.sunday.devfolio.enums.PortfolioSort;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;

/**
 * 포트폴리오 컨트롤러에 적용되는 사전 동작
 */
@ControllerAdvice(assignableTypes = {PortfolioController.class, PortfolioRestController.class})
public class PortfolioControllerAdvice {

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
                PortfolioSort sort = PortfolioSort.fromName(text);
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
}
