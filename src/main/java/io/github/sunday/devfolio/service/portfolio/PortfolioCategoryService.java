package io.github.sunday.devfolio.service.portfolio;

import io.github.sunday.devfolio.dto.portfolio.PortfolioCategoryDto;
import io.github.sunday.devfolio.entity.table.portfolio.PortfolioCategory;
import io.github.sunday.devfolio.repository.portfolio.PortfolioCategoryMapRepository;
import io.github.sunday.devfolio.repository.portfolio.PortfolioCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 포트폴리오 카테고리와 카테고리 매핑 Entity를 다루는 Service
 * 포트폴리오 카테고리 목록 조회, 포트폴리오 카테고리 매핑 등록 및 삭제
 */
@Service
@RequiredArgsConstructor
public class PortfolioCategoryService {
    private final PortfolioCategoryRepository portfolioCategoryRepository;
    private final PortfolioCategoryMapRepository portfolioCategoryMapRepository;

    /**
     * 포트폴리오 카테고리 전체 조회
     */
    public List<PortfolioCategoryDto> list() {
        List<PortfolioCategory> results = portfolioCategoryRepository.findAll();
        return results.stream()
                .map(category -> PortfolioCategoryDto.builder()
                        .categoryIdx(category.getCategoryIdx())
                        .name(category.getName())
                        .nameKo(category.getNameKo())
                        .description(category.getDescription())
                        .build()
                )
                .toList();
    }
    
    // Todo : 카테고리 매핑 추가
    
    // Todo : 카테고리 매핑 제거
}
