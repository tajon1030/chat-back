package com.example.demo.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMemberPK implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_SEQ", nullable = false)
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHAT_ROOM_ID", nullable = false)
    private ChatRoomEntity chatRoom;
}
