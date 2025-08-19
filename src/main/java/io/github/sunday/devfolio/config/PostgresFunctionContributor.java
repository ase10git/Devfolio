package io.github.sunday.devfolio.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.type.BasicType;
import org.hibernate.type.StandardBasicTypes;

/**
 * PostgreSQL 고유 구문을 Hibernate 함수 패턴에 등록하는 클래스
 * PostgreSQL의 Full-Text Search에 사용되는 고유 구문을 등록
 */
public class PostgresFunctionContributor implements FunctionContributor {

    /**
     * PostgreSQL 고유 구문 등록
     */
    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        BasicType<Boolean> booleanBasicType = functionContributions
                .getTypeConfiguration()
                .getBasicTypeRegistry()
                .resolve(StandardBasicTypes.BOOLEAN);

        BasicType<Float> floatBasicType = functionContributions
                .getTypeConfiguration()
                .getBasicTypeRegistry()
                .resolve(StandardBasicTypes.FLOAT);

        SqmFunctionRegistry registry = functionContributions.getFunctionRegistry();

        registry.registerPattern(
                "pgfts",
                "?1 @@ ?2",
                booleanBasicType
        );

        registry.registerPattern(
                "tsrank",
                "ts_rank(?1, ?2)",
                floatBasicType
        );

    }
}
