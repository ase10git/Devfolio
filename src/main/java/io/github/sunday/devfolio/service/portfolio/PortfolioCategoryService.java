package io.github.sunday.devfolio.service.portfolio;

import io.github.sunday.devfolio.dto.portfolio.PortfolioCategoryDto;
import io.github.sunday.devfolio.entity.table.portfolio.Portfolio;
import io.github.sunday.devfolio.entity.table.portfolio.PortfolioCategory;
import io.github.sunday.devfolio.entity.table.portfolio.PortfolioCategoryMap;
import io.github.sunday.devfolio.repository.portfolio.PortfolioCategoryMapRepository;
import io.github.sunday.devfolio.repository.portfolio.PortfolioCategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public boolean exists(Long categoryIdx) {
        if (categoryIdx == null) return true;
        return cachedCategories.stream()
                .anyMatch(category -> category.getCategoryIdx().equals(categoryIdx));
    }

    /**
     * 포트폴리오 카테고리 목록 조회
     */
    public List<PortfolioCategoryDto> getCategoriesByPortfolio(Portfolio portfolio) {
        List<PortfolioCategoryMap> mapList = portfolioCategoryMapRepository.findAllByPortfolio(portfolio);
        return mapList.stream()
                .map(map -> {
                    PortfolioCategory category = map.getCategory();
                    return PortfolioCategoryDto.builder()
                            .categoryIdx(category.getCategoryIdx())
                            .name(category.getName())
                            .nameKo(category.getNameKo())
                            .build();
                }).toList();
    }

    /**
     * 포트폴리오와 카테고리 매핑 데이터 추가
     */
    public void addPortfolioCategoryMap(Portfolio portfolio, List<Long> portfolioCategoryIdxList) {
        portfolioCategoryIdxList
                .forEach(idx -> {
                    PortfolioCategory category = portfolioCategoryRepository.findById(idx).orElse(null);
                    if (category == null) return;
                    PortfolioCategoryMap categoryMap = PortfolioCategoryMap.builder()
                            .portfolio(portfolio)
                            .category(category)
                            .build();
                    portfolioCategoryMapRepository.save(categoryMap);
                });
    }

    /**
     * 포트폴리오와 카테고리 매핑 수정
     */
    @Transactional
    public void editPortfolioCategoryMap(Portfolio portfolio, List<Long> portfolioCategoryIdxList) throws Exception {
        List<PortfolioCategoryMap> mapList = portfolioCategoryMapRepository.findAllByPortfolio(portfolio);
        List<Long> categoryList = mapList.stream().map(map -> map.getCategory().getCategoryIdx()).toList();

        // 제거된 매핑은 제거
        List<Long> deleteTargetList = categoryList.stream()
                .filter(idx -> !portfolioCategoryIdxList.contains(idx))
                .toList();
        if (!deleteTargetList.isEmpty()) {
            removePortfolioCategoryMap(portfolio, deleteTargetList);
        }

        // 추가된 매핑은 추가
        List<Long> addTargetList = portfolioCategoryIdxList.stream()
                        .filter(idx -> !categoryList.contains(idx))
                                .toList();
        if (!addTargetList.isEmpty()) {
            addPortfolioCategoryMap(portfolio, addTargetList);
        }
    }

    /**
     * 포트폴리오와 카테고리 매핑 관계 제거
     */
    public void removePortfolioCategoryMap(Portfolio portfolio, List<Long> portfolioCategoryIdxList) {
        portfolioCategoryIdxList
                .forEach(idx ->
                    portfolioCategoryMapRepository
                            .deleteByPortfolioAndCategory_CategoryIdx(portfolio, idx)
                );
    }
}
