package com.shop.onlyfit.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.onlyfit.domain.CustomServiceBoard;
import com.shop.onlyfit.domain.QCustomServiceBoard;
import com.shop.onlyfit.domain.type.CSBoardType;
import com.shop.onlyfit.dto.CustomServiceBoardDto;
import com.shop.onlyfit.dto.QCustomServiceBoardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static com.shop.onlyfit.domain.type.CSBoardType.NORMAL;

public class CSBoardRepositoryImpl implements CSBoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CSBoardRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<CustomServiceBoardDto> findByTitleContaining(String title, Pageable pageable) {
        QCustomServiceBoard csb = QCustomServiceBoard.customServiceBoard;
        QueryResults<CustomServiceBoardDto> results = queryFactory
                .select(new QCustomServiceBoardDto(
                        csb.id,
                        csb.user,
                        csb.title,
                        csb.boardType,
                        csb.count,
                        csb.content,
                        csb.createTime,
                        csb.secret
                ))
                .from(csb)
                .where(csb.title.contains(title), csb.boardType.eq(NORMAL))
                .orderBy(csb.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<CustomServiceBoardDto> content = results.getResults();
        System.out.println("내부 쿼리" + content);
        long total = results.getTotal();
        System.out.println("내부쿼리" + total);
        return new PageImpl<>(content, pageable, total);
    }
}
