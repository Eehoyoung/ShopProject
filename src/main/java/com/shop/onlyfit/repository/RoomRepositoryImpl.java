package com.shop.onlyfit.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.onlyfit.domain.SearchChat;
import com.shop.onlyfit.domain.chat.QChatRoom;
import com.shop.onlyfit.dto.ChatDslDto;
import com.shop.onlyfit.dto.QChatDslDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@Repository
public class RoomRepositoryImpl implements RoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public RoomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ChatDslDto> searchAllChat(Pageable pageable) {
        List<ChatDslDto> results = queryFactory
                .select(new QChatDslDto(
                        QChatRoom.chatRoom.roomId,
                        QChatRoom.chatRoom.sender,
                        QChatRoom.chatRoom.createdAt,
                        QChatRoom.chatRoom.modifiedAt
                ))
                .from(QChatRoom.chatRoom)
                .orderBy(QChatRoom.chatRoom.modifiedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(QChatRoom.chatRoom)
                .fetch().size();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<ChatDslDto> searchByConditionAllChat(SearchChat searchChat, Pageable pageable) {
        List<ChatDslDto> results = null;
        long total = 0;
        if (searchChat.getSearchCondition().equals("userid")) {
            results = queryFactory
                    .select(new QChatDslDto(
                            QChatRoom.chatRoom.roomId,
                            QChatRoom.chatRoom.sender,
                            QChatRoom.chatRoom.createdAt,
                            QChatRoom.chatRoom.modifiedAt
                    ))
                    .from(QChatRoom.chatRoom)
                    .where(loginIdEq(searchChat.getSearchKeyword()))
                    .orderBy(QChatRoom.chatRoom.modifiedAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            total = queryFactory
                    .selectFrom(QChatRoom.chatRoom)
                    .where(loginIdEq(searchChat.getSearchCondition()))
                    .fetch().size();

        } else if (searchChat.getSearchCondition().equals("username")) {
            results = queryFactory
                    .select(new QChatDslDto(
                            QChatRoom.chatRoom.roomId,
                            QChatRoom.chatRoom.sender,
                            QChatRoom.chatRoom.createdAt,
                            QChatRoom.chatRoom.modifiedAt
                    ))
                    .from(QChatRoom.chatRoom)
                    .where(nameEq(searchChat.getSearchKeyword()))
                    .orderBy(QChatRoom.chatRoom.modifiedAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            total = queryFactory
                    .selectFrom(QChatRoom.chatRoom)
                    .where(nameEq(searchChat.getSearchCondition()))
                    .fetch().size();
        }

        return new PageImpl<>(Objects.requireNonNull(results), pageable, total);
    }

    private BooleanExpression loginIdEq(String loginIdCondition) {
        if (StringUtils.isEmpty(loginIdCondition)) {
            return null;
        }
        return QChatRoom.chatRoom.sender.loginId.likeIgnoreCase("%" + loginIdCondition + "%");

    }

    private BooleanExpression nameEq(String nameCondition) {
        if (StringUtils.isEmpty(nameCondition)) {
            return null;
        }
        return QChatRoom.chatRoom.sender.name.likeIgnoreCase("%" + nameCondition + "%");
    }
}