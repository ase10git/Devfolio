package io.github.sunday.devfolio.config;

import io.github.sunday.devfolio.controller.community.CommunityController;
import io.github.sunday.devfolio.controller.community.CommunityRestController;
import io.github.sunday.devfolio.enums.community.CommunitySort;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;

/**
 * 커뮤니티 컨트롤러에 적용되는 사전 동작
 */
@ControllerAdvice(assignableTypes = {CommunityController.class, CommunityRestController.class})
public class CommunityControllerAdvice {

    /**
     * 커뮤니티 검색 요청이 들어올 때 DTO 내의 String에서 script를 제거
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
        binder.registerCustomEditor(CommunitySort.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                CommunitySort sort = CommunitySort.fromName(text);
                if (sort == null) {
                    sort = CommunitySort.UPDATED_AT;
                }
                setValue(sort);
            }
        });
    }

    /**
     * 커뮤니티 게시글, 댓글의 작성, 수정 요청이 들어올 때 DTO 내의 String에서 script를 제거
     */
    @InitBinder({"postRequest", "commentRequest"})
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