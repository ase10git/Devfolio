package io.github.sunday.devfolio.repository.portfolio;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.sunday.devfolio.dto.portfolio.PortfolioSearchRequestDto;
import io.github.sunday.devfolio.dto.portfolio.PortfolioSort;
import io.github.sunday.devfolio.entity.table.portfolio.Portfolio;
import io.github.sunday.devfolio.entity.table.portfolio.QPortfolio;
import io.github.sunday.devfolio.entity.table.portfolio.QPortfolioCategory;
import io.github.sunday.devfolio.entity.table.portfolio.QPortfolioCategoryMap;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

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

        List<Portfolio> list;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (searchRequestDto.getCategory() != null && !searchRequestDto.getCategory().isEmpty()) {
            booleanBuilder.and(portfolioCategory.name.equalsIgnoreCase(searchRequestDto.getCategory()));
        }

        String keyword = searchRequestDto.getKeyword();

        // 키워드 유무에 따른 Query 생성 및 결과 리스트 처리
        if (keyword != null && !keyword.isEmpty()) {
            StringTemplate tsQuery = Expressions.stringTemplate(
                    "websearch_to_tsquery('simple', {0})", keyword
            );

            NumberTemplate<Float> rank = Expressions.numberTemplate(
                    Float.class,
                    "tsrank({0}, {1})",
                    portfolio.searchVector, tsQuery
            );

            BooleanExpression keywordCondition = Expressions.booleanTemplate(
                    "pgfts({0}, {1})",
                    portfolio.searchVector, tsQuery
            );

            booleanBuilder.and(keywordCondition);

            JPAQuery<Tuple> query = queryFactory
                    .select(portfolio, rank)
                    .from(portfolio)
                    .join(portfolioCategoryMap)
                    .on(portfolio.eq(portfolioCategoryMap.portfolio))
                    .join(portfolioCategory)
                    .on(portfolioCategoryMap.category.eq(portfolioCategory))
                    .where(booleanBuilder)
                    .groupBy(portfolio)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(rank.desc().nullsLast(), getSortedColumn(searchRequestDto));

            list = query.fetch().stream()
                    .map(tuple -> tuple.get(portfolio))
                    .toList();
        } else {
            JPAQuery<Portfolio> query = queryFactory
                    .select(portfolio)
                    .from(portfolio)
                    .join(portfolioCategoryMap)
                    .on(portfolio.eq(portfolioCategoryMap.portfolio))
                    .join(portfolioCategory)
                    .on(portfolioCategoryMap.category.eq(portfolioCategory))
                    .where(booleanBuilder)
                    .groupBy(portfolio)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(getSortedColumn(searchRequestDto));

            list = query.fetch();
        }

        return list;
    }

    /**
     *  포트폴리오 정렬 기준 설정
     *  PortfolioSearchRequestDto의 정렬 기준과 방향을 사용
     */
    private OrderSpecifier<?> getSortedColumn(PortfolioSearchRequestDto searchRequestDto) {
        // 정렬 기준 설정
        String sortBy = searchRequestDto.getSort();
        PortfolioSort portfolioSort = PortfolioSort.fromFieldName(sortBy);
        if (portfolioSort == null) {
            portfolioSort = PortfolioSort.UPDATED_AT;
        }
        String fieldName = portfolioSort.getFieldName();

        // 정렬 방향 설정
        Sort.Direction direction = searchRequestDto.getDirection() != null ?
                searchRequestDto.getDirection()
                : Sort.Direction.DESC;
        com.querydsl.core.types.Order order = com.querydsl.core.types.Order.valueOf(direction.name());

        // 정렬 기준이 Entity의 필드와 일치하는지 확인
        PathBuilder<?> entityPath = new PathBuilder<>(Portfolio.class, "portfolio");
        ComparableExpressionBase<?> fieldPath = entityPath.getComparable(fieldName, Comparable.class);
        return new OrderSpecifier<>(order, fieldPath);
    }
}
