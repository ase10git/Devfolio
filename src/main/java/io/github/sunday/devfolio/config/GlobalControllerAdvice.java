package io.github.sunday.devfolio.config; // 패키지는 프로젝트 구조에 맞게 조정

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * 전역 컨트롤러 설정을 위한 클래스.
 * <p>
 * @ControllerAdvice 어노테이션을 통해 애플리케이션의 모든 컨트롤러에
 * 공통적으로 적용될 @InitBinder, @ExceptionHandler 등을 정의합니다.
 * </p>
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    /**
     * 모든 컨트롤러의 데이터 바인딩 시 String 타입에 대한 전처리를 수행합니다.
     * <p>
     * 입력된 문자열의 앞뒤 공백을 제거하고,
     * Jsoup 라이브러리를 사용하여 XSS(Cross-Site Scripting) 공격에 사용될 수 있는
     * 잠재적으로 위험한 HTML 태그를 필터링합니다.
     * </p>
     *
     * @param binder 데이터 바인딩 설정을 위한 WebDataBinder 객체
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true) {
            @Override
            public void setAsText(String text) {
                if (text == null) {
                    super.setValue(null);
                    return;
                }
                // CKEditor 내용 유지를 위해 기본적인 태그는 허용하는 relaxed Safelist 사용
                String safeText = Jsoup.clean(text, Safelist.relaxed());
                super.setAsText(safeText.trim());
            }
        });
    }
}