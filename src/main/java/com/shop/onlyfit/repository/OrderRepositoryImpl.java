package com.shop.onlyfit.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.onlyfit.domain.QOrder;
import com.shop.onlyfit.domain.QOrderItem;
import com.shop.onlyfit.domain.QUser;
import com.shop.onlyfit.domain.SearchOrder;
import com.shop.onlyfit.dto.MainPageOrderDto;
import com.shop.onlyfit.dto.QMainPageOrderDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderRepositoryImpl implements OrderRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public OrderRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<MainPageOrderDto> mainPageSearchAllOrder(Pageable pageable, String loginId) {
        QueryResults<MainPageOrderDto> results = queryFactory
                .select(new QMainPageOrderDto(
                        QOrder.order.id,
                        QOrderItem.orderItem.id,
                        QOrderItem.orderItem.item.id,
                        QUser.user.name,
                        QOrderItem.orderItem.item.itemName,
                        QOrder.order.orderedAt,
                        QOrderItem.orderItem.orderStatus,
                        QOrderItem.orderItem.orderPrice,
                        QOrderItem.orderItem.count,
                        QOrderItem.orderItem.item.imgUrl,
                        QOrderItem.orderItem.item.color
                ))
                .from(QOrder.order)
                .leftJoin(QOrderItem.orderItem).on(QOrderItem.orderItem.eq(QOrder.order.orderItemList.any()))
                .leftJoin(QUser.user).on(QUser.user.eq(QOrder.order.user))
                .where(QUser.user.loginId.eq(loginId), QOrderItem.orderItem.item.rep.eq(true))
                .orderBy(QOrderItem.orderItem.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MainPageOrderDto> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MainPageOrderDto> mainPageSearchAllOrderByCondition(SearchOrder searchOrder, Pageable pageable, String loginId) {
        QueryResults<MainPageOrderDto> results = queryFactory
                .select(new QMainPageOrderDto(
                        QOrder.order.id,
                        QOrderItem.orderItem.id,
                        QOrderItem.orderItem.item.id,
                        QUser.user.name,
                        QOrderItem.orderItem.item.itemName,
                        QOrder.order.orderedAt,
                        QOrderItem.orderItem.orderStatus,
                        QOrderItem.orderItem.orderPrice,
                        QOrderItem.orderItem.count,
                        QOrderItem.orderItem.item.imgUrl,
                        QOrderItem.orderItem.item.color
                ))
                .from(QOrder.order)
                .leftJoin(QOrderItem.orderItem).on(QOrderItem.orderItem.eq(QOrder.order.orderItemList.any()))
                .leftJoin(QUser.user).on(QUser.user.eq(QOrder.order.user))
                .where(QUser.user.loginId.eq(loginId),
                        QOrderItem.orderItem.item.rep.eq(true),
                        orderStatusEq(searchOrder.getOmode()),
                        betweenDate(searchOrder.getFirstdate(), searchOrder.getLastdate())
                )
                .orderBy(QOrderItem.orderItem.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MainPageOrderDto> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression betweenDate(String firstDate, String lastDate) {
        if (StringUtils.isEmpty(firstDate) && StringUtils.isEmpty(lastDate)) {

            LocalDate start = LocalDate.now().minusYears(10L);
            LocalDate end = LocalDate.now();

            return QOrder.order.orderedAt.between(start, end);
        } else if (StringUtils.isNotEmpty(firstDate) && StringUtils.isEmpty(lastDate)) {
            LocalDate start = LocalDate.parse(firstDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate end = LocalDate.now();

            return QOrder.order.orderedAt.between(start, end);
        } else if (StringUtils.isEmpty(firstDate) && StringUtils.isNotEmpty(lastDate)) {

            LocalDate start = LocalDate.now().minusYears(10L);
            LocalDate end = LocalDate.parse(lastDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            return QOrder.order.orderedAt.between(start, end);
        } else {
            LocalDate start = LocalDate.parse(firstDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate end = LocalDate.parse(lastDate);

            return QOrder.order.orderedAt.between(start, end);
        }
    }

    private BooleanExpression orderStatusEq(String orderStatusCondition) {
        return QOrderItem.orderItem.orderStatus.stringValue().eq(orderStatusCondition);
    }
}
