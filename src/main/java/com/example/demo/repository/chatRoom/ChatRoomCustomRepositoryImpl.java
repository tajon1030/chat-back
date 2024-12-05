package com.example.demo.repository.chatRoom;

import com.example.demo.dto.ChatRoom;
import com.example.demo.entity.QChatMemberEntity;
import com.example.demo.entity.QChatRoomEntity;
import com.example.demo.util.QuerydslUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ChatRoomCustomRepositoryImpl implements ChatRoomCustomRepository {
    private final JPAQueryFactory query;

    private final QChatRoomEntity qChatRoom = QChatRoomEntity.chatRoomEntity;
    private final QChatMemberEntity qChatMember = QChatMemberEntity.chatMemberEntity;

    @Override
    public Page<ChatRoom> findChatRoomPage(Pageable pageable) {
        List<ChatRoom> content = query.select(
                        Projections.bean(ChatRoom.class,
                                qChatRoom.id.as("roomId"),
                                qChatRoom.name,
                                qChatMember.count().as("userCount")
                        ))
                .from(qChatRoom)
                .leftJoin(qChatMember).on(qChatRoom.id.eq(qChatMember.chatRoom.id))
                .fetchJoin()
                .groupBy(qChatRoom.id, qChatRoom.name)
                .orderBy(QuerydslUtils.getSort(pageable, qChatRoom))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long total = query.select(qChatRoom.count())
                .from(qChatRoom)
                .fetchOne();

        return new PageImpl<ChatRoom>(content, pageable, total);
    }

    @Override
    public Page<ChatRoom> findEnteredChatRoomPage(Long userSeq, Pageable pageable) {
        return null;
    }

    @Override
    public Optional<ChatRoom> findChatRoomById(UUID id) {
        return Optional.ofNullable(query.select(
                        Projections.bean(ChatRoom.class,
                                qChatRoom.id.as("roomId"),
                                qChatRoom.name,
                                qChatMember.count().as("userCount")
                        ))
                .from(qChatRoom)
                .leftJoin(qChatMember)
                .on(qChatRoom.id.eq(qChatMember.chatRoom.id))
                .fetchJoin()
                .where(qChatRoom.id.eq(id))
                .groupBy(qChatRoom.id, qChatRoom.name)
                .fetchOne());
    }
}
