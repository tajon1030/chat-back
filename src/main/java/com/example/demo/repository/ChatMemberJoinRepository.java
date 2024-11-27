package com.example.demo.repository;

import com.example.demo.entity.ChatRoomEntity;
import com.example.demo.entity.QChatMemberEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ChatMemberJoinRepository {
    private final JPAQueryFactory query;

    private QChatMemberEntity qChatMember = QChatMemberEntity.chatMemberEntity;

    public long countByRoomId(UUID roomId) {
        return query.select(qChatMember)
                .where(qChatMember.chatMemberPK.chatRoom.eq(ChatRoomEntity.create(roomId.toString())))
                .stream().count();
    }

}
