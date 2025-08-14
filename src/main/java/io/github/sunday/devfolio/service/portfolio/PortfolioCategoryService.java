package io.github.sunday.devfolio.service.portfolio;

import io.github.sunday.devfolio.dto.portfolio.PortfolioCategoryDto;
import io.github.sunday.devfolio.entity.table.portfolio.PortfolioCategory;
import io.github.sunday.devfolio.repository.portfolio.PortfolioCategoryMapRepository;
import io.github.sunday.devfolio.repository.portfolio.PortfolioCategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 포트폴리오 카테고리와 카테고리 매핑 Entity를 다루는 Service
 * 포트폴리오 카테고리 목록 조회, 포트폴리오 카테고리 매핑 등록 및 삭제
 */
@Service
@Getter
@RequiredArgsConstructor
public class PortfolioCategoryService {
    private final PortfolioCategoryRepository portfolioCategoryRepository;
    private final PortfolioCategoryMapRepository portfolioCategoryMapRepository;
    private List<PortfolioCategoryDto> cachedCategories;

    /**
     * 포트폴리오 카테고리 전체를 미리 조회
     */
    @PostConstruct
    public void loadCategories() {
        List<PortfolioCategory> results = portfolioCategoryRepository.findAll();
        cachedCategories =  results.stream()
                .map(category -> PortfolioCategoryDto.builder()
                        .categoryIdx(category.getCategoryIdx())
                        .name(category.getName())
                        .nameKo(category.getNameKo())
                        .description(category.getDescription())
                        .build()
                )
                .toList();
    }

    /**
     * 카테고리 이름으로 존재하는 카테고리인지 검증
     */
    public boolean exists(String categoryName) {
        if (categoryName == null || categoryName.isEmpty()) return true;
        return cachedCategories.stream()
                .anyMatch(category -> category.getName().equalsIgnoreCase(categoryName));
    }

    // Todo : 카테고리 매핑 추가
    
    // Todo : 카테고리 매핑 제거
}
