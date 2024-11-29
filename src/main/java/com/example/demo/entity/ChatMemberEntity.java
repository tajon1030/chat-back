package com.example.demo.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "CHAT_MEMBER")
@IdClass(ChatMemberPK.class)
public class ChatMemberEntity {

    @Id
    @Column(name = "MEBMER_SEQ")
    private Long memberSeq;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHAT_ROOM_ID")
    private ChatRoomEntity chatRoom;

}
