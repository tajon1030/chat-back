package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
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
