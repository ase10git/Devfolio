package io.github.sunday.devfolio.repository.community;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.sunday.devfolio.dto.community.CommunitySearchRequestDto;
import io.github.sunday.devfolio.entity.table.community.Category;
import io.github.sunday.devfolio.entity.table.community.CommunityPost;
import io.github.sunday.devfolio.entity.table.community.QCommunityPost;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * 게시글 Entity를 대상으로 하는 QueryDSL 리포지토리
 */
@Repository
@RequiredArgsConstructor
public class CommunityQueryDslRepository {
    private final JPAQueryFactory queryFactory;


    /**
     * 키워드와 카테고리로 포트폴리오 조회
     * 정렬 기준과 방향 설정 가능
     * 페이지네이션 적용
     */
    public Page<CommunityPost> findAllByKeywordAndCategory(CommunitySearchRequestDto searchRequestDto, Pageable pageable) {
        QCommunityPost communityPost = QCommunityPost.communityPost;

        // 조건 설정
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        String keyword = searchRequestDto.getKeyword();
        String categoryName = searchRequestDto.getCategoryName();
        if (categoryName != null) {
            Category category = Category.getCategory(categoryName);
            booleanBuilder.and(communityPost.category.eq(category));
        }

        // 키워드 조건별 rank 생성 및 booleanBuilder 업데이트
        NumberTemplate<Float> rank = buildTsQueryCondition(communityPost, booleanBuilder, keyword);

        // 콘텐츠 조회 쿼리 실행
        List<CommunityPost> content = executeContentQuery(communityPost, booleanBuilder, rank, pageable);

        // 전체 개수 조회 쿼리 실행
        Long totalCount = executeCountQuery(communityPost, booleanBuilder);

        // 쿼리문 실행
        return new PageImpl<>(content, pageable, totalCount);
    }

    /**
     * 공통 쿼리 요청 로직
     */
    private List<CommunityPost> executeContentQuery(
            QCommunityPost communityPost,
            BooleanBuilder booleanBuilder,
            NumberTemplate<Float> rank,
            Pageable pageable
    ) {
        OrderSpecifier<?>[] orderSpecifiers = buildOrderSpecifier(pageable, rank, communityPost);

        JPAQuery<?> query = (rank != null) ?
                queryFactory.select(communityPost, rank).from(communityPost)
                : queryFactory.select(communityPost).from(communityPost);

        query.where(booleanBuilder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifiers);

        if (rank != null) {
            List<Tuple> results = (List<Tuple>) query.fetch();
            return results.stream()
                    .map(tuple -> tuple.get(0, CommunityPost.class))
                    .filter(Objects::nonNull)
                    .toList();
        }
        return (List<CommunityPost>) query.fetch();
    }

    /**
     *  전체 개수 조회 로직
     */
    private Long executeCountQuery(QCommunityPost communityPost, BooleanBuilder booleanBuilder) {
        return queryFactory
                .select(communityPost.count())
                .from(communityPost)
                .where(booleanBuilder)
                .fetchOne();
    }

    /**
     * 키워드 필터링 옵션을 추가
     */
    private NumberTemplate<Float> buildTsQueryCondition(QCommunityPost communityPost, BooleanBuilder booleanBuilder, String keyword) {
        if (keyword == null || keyword.isEmpty()) return null;

        StringTemplate tsQuery = Expressions.stringTemplate(
                "websearch_to_tsquery('simple', {0})", keyword
        );
        BooleanExpression keywordCondition = Expressions.booleanTemplate(
                "pgfts({0}, {1})",
                communityPost.searchVector, tsQuery
        );
        NumberTemplate<Float> rank = Expressions.numberTemplate(
                Float.class,
                "tsrank({0}, {1})",
                communityPost.searchVector, tsQuery
        );

        booleanBuilder.and(keywordCondition);
        return rank;
    }

    /**
     *  게시글 정렬 기준 설정
     */
    private OrderSpecifier<?> getSortedColumn(Pageable pageable) {
        // category 기준임
        Sort sort = pageable.getSort();
        return sort.stream()
                .findFirst()
                .map(order -> {
                    PathBuilder<?> pathBuilder = new PathBuilder<>(CommunityPost.class, "communityPost");

                    // 정렬 방향
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;

                    // 정렬 기준 필드명
                    String property = order.getProperty();
                    return new OrderSpecifier(direction, pathBuilder.get(property));
                })
                .orElse(null);
    }

    /**
     * 전체 정렬 기준 설정
     * 검색 키워드에 따른 정렬 기준과 포트폴리오 컬럼 기반 정렬 기준 설정
     */
    private OrderSpecifier<?>[] buildOrderSpecifier(
            Pageable pageable,
            NumberTemplate<Float> rank,
            QCommunityPost communityPost
    ) {
        OrderSpecifier<?> sortedColumn = getSortedColumn(pageable);
        if (rank != null) {
            return new OrderSpecifier[]{
                    rank.desc().nullsLast(),
                    sortedColumn,
                    communityPost.postIdx.desc()
            };
        }
        return new OrderSpecifier[]{sortedColumn, communityPost.postIdx.desc()};
    }
}
