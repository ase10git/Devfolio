package io.github.sunday.devfolio.config;

import io.github.sunday.devfolio.enums.CommunitySort; // import 필요
import io.github.sunday.devfolio.enums.PortfolioSort;   // import 필요
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;

@ControllerAdvice
public class GlobalControllerAdvice {

    /**
     * 모든 String 타입 입력에 대해 기본 XSS 방어 및 공백 제거를 수행합니다.
     * 가장 엄격한 Safelist.basic()을 사용합니다.
     */
    @InitBinder
    public void initStringBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true) {
            @Override
            public void setAsText(String text) {
                if (text == null) {
                    super.setValue(null);
                    return;
                }
                // HTML 태그가 거의 없는 일반적인 문자열 필드를 위한 설정
                String safeText = Jsoup.clean(text, Safelist.basic());
                super.setAsText(safeText.trim());
            }
        });
    }

    /**
     * CommunitySort Enum 타입 변환을 처리합니다.
     */
    @InitBinder
    public void initCommunitySortBinder(WebDataBinder binder) {
        binder.registerCustomEditor(CommunitySort.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                CommunitySort sort = CommunitySort.fromName(text);
                if (sort == null) {
                    sort = CommunitySort.UPDATED_AT; // 기본값
                }
                setValue(sort);
            }
        });
    }

    /**
     * PortfolioSort Enum 타입 변환을 처리합니다.
     */
    @InitBinder
    public void initPortfolioSortBinder(WebDataBinder binder) {
        binder.registerCustomEditor(PortfolioSort.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                PortfolioSort sort = PortfolioSort.fromName(text);
                if (sort == null) {
                    sort = PortfolioSort.UPDATED_AT; // 기본값
                }
                setValue(sort);
            }
        });
    }
}