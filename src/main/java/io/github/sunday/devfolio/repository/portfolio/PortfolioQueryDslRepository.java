package io.github.sunday.devfolio.repository.portfolio;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.sunday.devfolio.dto.portfolio.PortfolioSearchRequestDto;
import io.github.sunday.devfolio.enums.portfolio.PortfolioSort;
import io.github.sunday.devfolio.entity.table.portfolio.Portfolio;
import io.github.sunday.devfolio.entity.table.portfolio.QPortfolio;
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

        // 조건 설정
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        String keyword = searchRequestDto.getKeyword();
        Long filterCategoryIdx = searchRequestDto.getCategoryIdx();

        // 키워드 조건별 rank 생성 및 booleanBuilder 업데이트
        NumberTemplate<Float> rank = buildTsQueryCondition(portfolio, booleanBuilder, keyword);
        // 정렬 순서 설정
        OrderSpecifier<?>[] orderSpecifiers = buildOrderSpecifier(searchRequestDto, rank, QPortfolio.portfolio);

        // 필터링 조건 업데이트
        buildCategoryCondition(portfolio, portfolioCategoryMap, booleanBuilder, filterCategoryIdx);

        // 쿼리문 실행
        return executePortfolioQuery(portfolio, booleanBuilder, rank, orderSpecifiers, pageable);
    }

    /**
     * 공통 쿼리 요청 로직
     */
    private List<Portfolio> executePortfolioQuery(
            QPortfolio portfolio,
            BooleanBuilder booleanBuilder,
            NumberTemplate<Float> rank,
            OrderSpecifier<?>[] orderSpecifiers,
            Pageable pageable
    ) {
        JPAQuery<?> query = (rank != null) ?
                queryFactory.select(portfolio, rank).from(portfolio)
                : queryFactory.select(portfolio).from(portfolio);

        query.where(booleanBuilder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifiers);

        if (rank != null) {
            return ((JPAQuery<Tuple>) query).fetch()
                    .stream()
                    .map(tuple -> tuple.get(portfolio))
                    .toList();
        }
        return ((JPAQuery<Portfolio>) query).fetch();
    }

    /**
     * 카테고리 필터링 옵션을 추가
     */
    private void buildCategoryCondition(
            QPortfolio portfolio,
            QPortfolioCategoryMap portfolioCategoryMap,
            BooleanBuilder booleanBuilder,
            Long filterCategoryIdx
    ) {
        if (filterCategoryIdx == null) return;

        // 카테고리로 필터링한 포트폴리오 IDX 목록
        List<Long> filterdPortfolioIdx = queryFactory
                .select(portfolioCategoryMap.portfolio.portfolioIdx)
                .from(portfolioCategoryMap)
                .where(portfolioCategoryMap.category.categoryIdx.eq(filterCategoryIdx))
                .orderBy(portfolioCategoryMap.portfolio.portfolioIdx.desc())
                .fetch();

        booleanBuilder.and(portfolio.portfolioIdx.in(filterdPortfolioIdx));
    }

    /**
     * 키워드 필터링 옵션을 추가
     */
    private NumberTemplate<Float> buildTsQueryCondition(QPortfolio portfolio, BooleanBuilder booleanBuilder, String keyword) {
        if (keyword == null || keyword.isEmpty()) return null;

        StringTemplate tsQuery = Expressions.stringTemplate(
                "websearch_to_tsquery('simple', {0})", keyword
        );
        BooleanExpression keywordCondition = Expressions.booleanTemplate(
                "pgfts({0}, {1})",
                portfolio.searchVector, tsQuery
        );
        NumberTemplate<Float> rank = Expressions.numberTemplate(
                Float.class,
                "tsrank({0}, {1})",
                portfolio.searchVector, tsQuery
        );

        booleanBuilder.and(keywordCondition);
        return rank;
    }

    /**
     *  포트폴리오 정렬 기준 설정
     *  PortfolioSearchRequestDto의 정렬 기준과 방향을 사용
     */
    private OrderSpecifier<?> getSortedColumn(PortfolioSearchRequestDto searchRequestDto) {
        // 정렬 기준 설정
        PortfolioSort portfolioSort = searchRequestDto.getSort();
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

    /**
     * 전체 정렬 기준 설정
     * 검색 키워드에 따른 정렬 기준과 포트폴리오 컬럼 기반 정렬 기준 설정
     */
    private OrderSpecifier<?>[] buildOrderSpecifier(
            PortfolioSearchRequestDto searchRequestDto,
            NumberTemplate<Float> rank,
            QPortfolio portfolio
    ) {
        OrderSpecifier<?> sortedColumn = getSortedColumn(searchRequestDto);
        if (rank != null) {
            return new OrderSpecifier[]{
                    rank.desc().nullsLast(),
                    sortedColumn,
                    portfolio.portfolioIdx.desc()
            };
        }
        return new OrderSpecifier[]{sortedColumn, portfolio.portfolioIdx.desc()};
    }
}
