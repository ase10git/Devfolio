package io.github.sunday.devfolio.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Alan AI 답변을 담는 Dto
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlanResponseDto {
    /**
     * 동작
     */
    private Action action;

    /**
     * 답변 내용
     */
    private String content;

    public class Action {
        /**
         * 동작 타입
         */
        protected String name;

        /**
         * AI 응답 내용으로 추정되는 데이터
         */
        protected String speak;

        public Action() {}

        public Action(String name, String speak) {
            this.name = name;
            this.speak = speak;
        }

        public String getName() {
            return name;
        }

        public String getSpeak() {
            return speak;
        }
    }
}
