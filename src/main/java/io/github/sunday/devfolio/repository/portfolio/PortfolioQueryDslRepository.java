package io.github.sunday.devfolio.repository.portfolio;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.sunday.devfolio.dto.portfolio.PortfolioSearchRequestDto;
import io.github.sunday.devfolio.entity.table.portfolio.Portfolio;
import io.github.sunday.devfolio.entity.table.portfolio.QPortfolio;
import io.github.sunday.devfolio.entity.table.portfolio.QPortfolioCategory;
import io.github.sunday.devfolio.entity.table.portfolio.QPortfolioCategoryMap;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 포트폴리오 Entity를 대상으로 하는 QueryDSL 리포지토리
 */
@Repository
@RequiredArgsConstructor
public class PortfolioQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    /**
     * 키워드와 카테고리로 포트폴리오 조회
     * 정렬 기준과 방향 설정 가능
     * 페이지네이션 적용
     */
    public List<Portfolio> findAllByKeywordAndCategory(PortfolioSearchRequestDto searchRequestDto, Pageable pageable) {
        QPortfolio portfolio = QPortfolio.portfolio;
        QPortfolioCategoryMap portfolioCategoryMap = QPortfolioCategoryMap.portfolioCategoryMap;
        QPortfolioCategory portfolioCategory = QPortfolioCategory.portfolioCategory;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        StringTemplate tsQuery = Expressions.stringTemplate(
                "websearch_to_tsquery('simple', {0})",
                searchRequestDto.getKeyword() != null ?
                        searchRequestDto.getKeyword() : ""
        );

        NumberTemplate<Float> rank = Expressions.numberTemplate(
                Float.class,
                "ts_rank({0}, {1})",
                portfolio.searchVector, tsQuery
        );

        BooleanExpression keywordCondition = Expressions.booleanTemplate(
                "{0} @@ {1}",
                portfolio.searchVector, tsQuery
        );

        if (searchRequestDto.getKeyword() != null && !searchRequestDto.getKeyword().isEmpty()) {
            booleanBuilder.and(keywordCondition);
        }

        if (searchRequestDto.getCategory() != null && !searchRequestDto.getCategory().isEmpty()) {
            booleanBuilder.and(portfolioCategory.name.eq(searchRequestDto.getCategory()));
        }

        return queryFactory.selectFrom(portfolio)
                .join(portfolioCategoryMap)
                .on(portfolio.eq(portfolioCategoryMap.portfolio))
                .join(portfolioCategory)
                .on(portfolioCategoryMap.category.eq(portfolioCategory))
                .where(booleanBuilder)
                .orderBy(
                        rank.desc().nullsLast(),
                        getSortedColumn(searchRequestDto)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    /**
     *  포트폴리오 정렬 기준 설정
     *  PortfolioSearchRequestDto의 정렬 기준과 방향을 사용
     */
    private OrderSpecifier<?> getSortedColumn(PortfolioSearchRequestDto searchRequestDto) {
        Map<String, String> fieldMapping = Map.of(
                "updatedAt", "updatedAt",
                "commentCount", "commentCount",
                "views", "views",
                "likeCount", "likeCount"
        );

        Order order = searchRequestDto.getOrder() != null ? searchRequestDto.getOrder() : Order.DESC;
        String sortKey = Optional.ofNullable(searchRequestDto.getSort()).orElse("updatedAt");
        String fieldName = fieldMapping.get(sortKey);

        PathBuilder<?> entityPath = new PathBuilder<>(Portfolio.class, "portfolio");
        ComparableExpressionBase<?> fieldPath = entityPath.getComparable(fieldName, Comparable.class);

        return order == Order.ASC
                ? fieldPath.asc().nullsLast()
                : fieldPath.desc().nullsLast();
    }
}
