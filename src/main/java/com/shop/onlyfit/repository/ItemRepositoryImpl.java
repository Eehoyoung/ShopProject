package com.shop.onlyfit.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.onlyfit.domain.Item;
import com.shop.onlyfit.domain.QCart;
import com.shop.onlyfit.domain.QItem;
import com.shop.onlyfit.domain.SearchItem;
import com.shop.onlyfit.dto.QWeeklyBestDto;
import com.shop.onlyfit.dto.WeeklyBestDto;
import com.shop.onlyfit.dto.item.ItemDto;
import com.shop.onlyfit.dto.item.QItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ItemRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Long getLatestItemIdx() {

        return queryFactory
                .select(QItem.item.itemIdx)
                .from(QItem.item)
                .orderBy(QItem.item.itemIdx.desc())
                .fetchFirst();
    }

    @Override
    public Page<ItemDto> searchAllItem(Pageable pageable) {
        List<ItemDto> results = queryFactory
                .select(new QItemDto(
                        QItem.item.id,
                        QItem.item.itemName,
                        QItem.item.firstCategory,
                        QItem.item.price,
                        QItem.item.saleStatus,
                        QItem.item.imgUrl,
                        QItem.item.color,
                        QItem.item.rep,
                        QItem.item.itemIdx
                ))
                .from(QItem.item)
                .where(QItem.item.rep.eq(true))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory // count 쿼리
                .selectFrom(QItem.item)
                .fetch().size();


        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<ItemDto> searchAllItemByCondition(SearchItem search, Pageable pageable) {

        List<ItemDto> results;
        long total;// count 쿼리
        if (search.getCmode().equals("whole")) {
            results = queryFactory
                    .select(new QItemDto(
                            QItem.item.id,
                            QItem.item.itemName,
                            QItem.item.firstCategory,
                            QItem.item.price,
                            QItem.item.saleStatus,
                            QItem.item.imgUrl,
                            QItem.item.color,
                            QItem.item.rep,
                            QItem.item.itemIdx
                    ))
                    .from(QItem.item)
                    .where(QItem.item.rep.eq(true), saleStatusEq(search.getSalestatus()), itemNameEq(search.getItem_name()))
                    .orderBy(QItem.item.id.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            total = queryFactory // count 쿼리
                    .selectFrom(QItem.item)
                    .where(QItem.item.rep.eq(true), saleStatusEq(search.getSalestatus()), itemNameEq(search.getItem_name()))
                    .fetch().size();

        } else {
            results = queryFactory
                    .select(new QItemDto(
                            QItem.item.id,
                            QItem.item.itemName,
                            QItem.item.firstCategory,
                            QItem.item.price,
                            QItem.item.saleStatus,
                            QItem.item.imgUrl,
                            QItem.item.color,
                            QItem.item.rep,
                            QItem.item.itemIdx
                    ))
                    .from(QItem.item)
                    .where(QItem.item.rep.eq(true), saleStatusEq(search.getSalestatus()), cmodeEq(search.getCmode()), itemNameEq(search.getItem_name()))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            total = queryFactory // count 쿼리
                    .selectFrom(QItem.item)
                    .where(QItem.item.rep.eq(true), saleStatusEq(search.getSalestatus()), cmodeEq(search.getCmode()), itemNameEq(search.getItem_name()))
                    .fetch().size();

        }
        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<ItemDto> searchAllItemByConditionMaket(String loginId, SearchItem search, Pageable pageable) {

        List<ItemDto> results;
        long total;// count 쿼리
        if (search.getCmode().equals("whole")) {
            results = queryFactory
                    .select(new QItemDto(
                            QItem.item.id,
                            QItem.item.itemName,
                            QItem.item.firstCategory,
                            QItem.item.price,
                            QItem.item.saleStatus,
                            QItem.item.imgUrl,
                            QItem.item.color,
                            QItem.item.rep,
                            QItem.item.itemIdx
                    ))
                    .from(QItem.item)
                    .where(QItem.item.market.seller.loginId.eq(loginId), QItem.item.rep.eq(true), saleStatusEq(search.getSalestatus()), itemNameEq(search.getItem_name()))
                    .orderBy(QItem.item.id.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            total = queryFactory // count 쿼리
                    .selectFrom(QItem.item)
                    .where(QItem.item.market.seller.loginId.eq(loginId), QItem.item.rep.eq(true), saleStatusEq(search.getSalestatus()), itemNameEq(search.getItem_name()))
                    .fetch().size();

        } else {
            results = queryFactory
                    .select(new QItemDto(
                            QItem.item.id,
                            QItem.item.itemName,
                            QItem.item.firstCategory,
                            QItem.item.price,
                            QItem.item.saleStatus,
                            QItem.item.imgUrl,
                            QItem.item.color,
                            QItem.item.rep,
                            QItem.item.itemIdx
                    ))
                    .from(QItem.item)
                    .where(QItem.item.market.seller.loginId.eq(loginId), QItem.item.rep.eq(true), saleStatusEq(search.getSalestatus()), cmodeEq(search.getCmode()), itemNameEq(search.getItem_name()))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            total = queryFactory // count 쿼리
                    .selectFrom(QItem.item)
                    .where(QItem.item.market.seller.loginId.eq(loginId), QItem.item.rep.eq(true), saleStatusEq(search.getSalestatus()), cmodeEq(search.getCmode()), itemNameEq(search.getItem_name()))
                    .fetch().size();

        }
        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<ItemDto> findAllItem(Pageable pageable, String firstCategory, String secondCategory) {
        List<ItemDto> results = queryFactory
                .selectDistinct(new QItemDto(
                        QItem.item.itemIdx,
                        QItem.item.itemName,
                        QItem.item.imgUrl,
                        QItem.item.price,
                        QItem.item.firstCategory,
                        QItem.item.secondCategory,
                        QItem.item.saleStatus,
                        QItem.item.rep
                ))
                .from(QItem.item)
                .where(QItem.item.rep.eq(true),
                        QItem.item.firstCategory.eq(firstCategory),
                        QItem.item.secondCategory.eq(secondCategory)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory // count 쿼리
                .selectFrom(QItem.item)
                .where(QItem.item.rep.eq(true),
                        QItem.item.firstCategory.eq(firstCategory),
                        QItem.item.secondCategory.eq(secondCategory)
                ).fetch().size();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<ItemDto> findAllItemByMarketId(Pageable pageable, Long marketId) {
        List<ItemDto> results = queryFactory
                .select(new QItemDto(
                        QItem.item.id,
                        QItem.item.itemName,
                        QItem.item.firstCategory,
                        QItem.item.price,
                        QItem.item.saleStatus,
                        QItem.item.imgUrl,
                        QItem.item.color,
                        QItem.item.rep,
                        QItem.item.itemIdx
                ))
                .from(QItem.item)
                .where(QItem.item.rep.eq(true), QItem.item.market.marketId.eq(marketId))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory // count 쿼리
                .selectFrom(QItem.item)
                .where(QItem.item.rep.eq(true), QItem.item.market.marketId.eq(marketId))
                .fetch().size();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public ItemDto findAllItemInCart(Long itemId) {

        return queryFactory
                .select(new QItemDto(
                        QItem.item.itemIdx,
                        QItem.item.imgUrl,
                        QItem.item.itemName,
                        QItem.item.color,
                        QItem.item.price,
                        QCart.cart.cartCount
                ))
                .from(QItem.item)
                .leftJoin(QCart.cart).on(QCart.cart.eq(QItem.item.cartList.any()))
                .where(QItem.item.rep.eq(true),
                        QItem.item.id.eq(itemId)
                )
                .fetchOne();
    }

    @Override
    public List<WeeklyBestDto> findWeeklyBestItem(String firstCategory, String secondCategory, boolean rep) {

        return queryFactory
                .selectDistinct(new QWeeklyBestDto(
                        QItem.item.itemIdx,
                        QItem.item.itemName,
                        QItem.item.price,
                        QItem.item.itemInfo,
                        QItem.item.imgUrl
                ))
                .from(QItem.item)
                .where(QItem.item.rep.eq(true),
                        QItem.item.firstCategory.eq(firstCategory),
                        QItem.item.secondCategory.eq(secondCategory)
                )
                .limit(9L)
                .fetch();
    }

    @Override
    public List<WeeklyBestDto> findNewArrivalItem(String firstCategory, String secondCategory, boolean rep) {

        return queryFactory
                .selectDistinct(new QWeeklyBestDto(
                        QItem.item.itemIdx,
                        QItem.item.itemName,
                        QItem.item.price,
                        QItem.item.imgUrl
                ))
                .from(QItem.item)
                .where(QItem.item.rep.eq(true),
                        QItem.item.firstCategory.eq(firstCategory),
                        QItem.item.secondCategory.eq(secondCategory)
                )
                .limit(9L)
                .fetch();
    }

    @Override
    public Page<ItemDto> searchAllItemByloginId(String loginId, Pageable pageable) {
        List<ItemDto> results = queryFactory
                .select(new QItemDto(
                        QItem.item.id,
                        QItem.item.itemName,
                        QItem.item.firstCategory,
                        QItem.item.price,
                        QItem.item.saleStatus,
                        QItem.item.imgUrl,
                        QItem.item.color,
                        QItem.item.rep,
                        QItem.item.itemIdx
                ))
                .from(QItem.item)
                .where(QItem.item.market.seller.loginId.eq(loginId))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory // count 쿼리
                .selectFrom(QItem.item)
                .where(QItem.item.market.seller.loginId.eq(loginId))
                .fetch().size();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<ItemDto> searchAllItemByMarketId(Long marketId, Pageable pageable) {
        List<ItemDto> results = queryFactory
                .select(new QItemDto(
                        QItem.item.id,
                        QItem.item.itemName,
                        QItem.item.firstCategory,
                        QItem.item.price,
                        QItem.item.saleStatus,
                        QItem.item.imgUrl,
                        QItem.item.color,
                        QItem.item.rep,
                        QItem.item.itemIdx
                ))
                .from(QItem.item)
                .where(QItem.item.market.marketId.eq(marketId))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory // count 쿼리
                .selectFrom(QItem.item)
                .where(QItem.item.market.marketId.eq(marketId))
                .fetch().size();

        return new PageImpl<>(results, pageable, total);
    }


    private BooleanExpression saleStatusEq(String saleStatusCondition) {
        if (StringUtils.isEmpty(saleStatusCondition)) {
            return null;
        }
        return QItem.item.saleStatus.equalsIgnoreCase(saleStatusCondition);
    }

    private BooleanExpression cmodeEq(String cmodeCondition) {
        if (StringUtils.isEmpty(cmodeCondition)) {
            return null;
        }
        return QItem.item.firstCategory.equalsIgnoreCase(cmodeCondition);
    }

    private BooleanExpression itemNameEq(String itemNameCondition) {
        if (StringUtils.isEmpty(itemNameCondition)) {
            return null;
        }
        return QItem.item.itemName.likeIgnoreCase("%" + itemNameCondition + "%");
    }

    @Override
    public Long searchMaxItemIdx() {
        QItem itemSub = new QItem("itemSub");
        List<Item> findItem = queryFactory
                .selectFrom(QItem.item)
                .where(QItem.item.itemIdx.eq(
                        JPAExpressions
                                .select(itemSub.itemIdx.max())
                                .from(itemSub)
                ))
                .fetch();
        return findItem.get(0).getItemIdx();
    }


}
