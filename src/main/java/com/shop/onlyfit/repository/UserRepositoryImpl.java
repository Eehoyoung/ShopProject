package com.shop.onlyfit.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.onlyfit.domain.QUser;
import com.shop.onlyfit.domain.SearchUser;
import com.shop.onlyfit.dto.user.QUserDto;
import com.shop.onlyfit.dto.user.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<UserDto> searchAll(Pageable pageable) {
        List<UserDto> results = queryFactory
                .select(new QUserDto(
                        QUser.user.id,
                        QUser.user.name,
                        QUser.user.loginId,
                        QUser.user.userGrade,
                        QUser.user.phoneNumber,
                        QUser.user.visitCount,
                        QUser.user.orderCount,
                        QUser.user.createdAt
                ))
                .from(QUser.user)
                .orderBy(QUser.user.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(QUser.user)
                .fetch().size();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<UserDto> searchByCondition(SearchUser search, Pageable pageable) {
        List<UserDto> results = null;
        long total = 0;
        if (search.getSearchCondition().equals("userid")) {
            results = queryFactory
                    .select(new QUserDto(
                            QUser.user.id,
                            QUser.user.name,
                            QUser.user.loginId,
                            QUser.user.userGrade,
                            QUser.user.phoneNumber,
                            QUser.user.visitCount,
                            QUser.user.orderCount,
                            QUser.user.createdAt
                    ))
                    .from(QUser.user)
                    .where(loginIdEq(search.getSearchKeyword()))
                    .orderBy(QUser.user.createdAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            total = queryFactory
                    .selectFrom(QUser.user)
                    .where(loginIdEq(search.getSearchCondition()))
                    .fetch().size();

        } else if (search.getSearchCondition().equals("username")) {
            results = queryFactory
                    .select(new QUserDto(
                            QUser.user.id,
                            QUser.user.name,
                            QUser.user.loginId,
                            QUser.user.userGrade,
                            QUser.user.phoneNumber,
                            QUser.user.visitCount,
                            QUser.user.orderCount,
                            QUser.user.createdAt
                    ))
                    .from(QUser.user)
                    .where(nameEq(search.getSearchKeyword()))
                    .orderBy(QUser.user.createdAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            total = queryFactory
                    .selectFrom(QUser.user)
                    .where(nameEq(search.getSearchKeyword()))
                    .fetch().size();
        }

        return new PageImpl<>(Objects.requireNonNull(results), pageable, total);
    }

    private BooleanExpression loginIdEq(String loginIdCondition) {
        if (StringUtils.isEmpty(loginIdCondition)) {
            return null;
        }
        return QUser.user.loginId.likeIgnoreCase("%" + loginIdCondition + "%");
    }

    private BooleanExpression nameEq(String nameCondition) {
        if (StringUtils.isEmpty(nameCondition)) {
            return null;
        }
        return QUser.user.name.likeIgnoreCase("%" + nameCondition + "%");
    }
}

