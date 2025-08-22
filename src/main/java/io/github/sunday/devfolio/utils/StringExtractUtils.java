package io.github.sunday.devfolio.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.sunday.devfolio.dto.common.AlanResponseDto;
import io.github.sunday.devfolio.dto.portfolio.PortfolioTemplateDto;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Alan AI의 Content 내의 JSON을 가공하는 유틸 클래스
 */
public class StringExtractUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Alan AI에서 응답 내용 중 JSON 추출하기
     */
    public static JsonNode extractJsonArray(AlanResponseDto response) throws Exception {
        if (response == null || response.getContent() == null || response.getContent().isEmpty()) {
            throw new IllegalArgumentException("응답 데이터나 내용이 비어있습니다");
        }

        // content 문자열 가져오기
        String content = response.getContent();

        // ```json ... ``` 구간만 추출
        Pattern pattern = Pattern.compile("```json\\s*(.*?)\\s*```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        if (!matcher.find()) {
            throw new IllegalArgumentException("content에 json 블록이 없습니다.");
        }

        String jsonPart = matcher.group(1); // ```json 과 ``` 사이 문자열

        // JSON 파싱
        return mapper.readTree(jsonPart);
    }

    /**
     * 추출한 JSON을 포트폴리오 템플릿 형식으로 변환하기
     */
    public static List<PortfolioTemplateDto> parseToTemplateResponse(JsonNode jsonNode) {
        if (jsonNode == null || !jsonNode.isArray()) {
            throw new IllegalArgumentException("JSON 데이터가 배열 형식이 아닙니다.");
        }
        return mapper.convertValue(jsonNode, new TypeReference<List<PortfolioTemplateDto>>() {});
    }
}
