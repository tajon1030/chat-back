package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChatMemberPK implements Serializable {

    private Long memberSeq;
    private UUID chatRoom;

    public ChatMemberPK(Long userSeq, String roomId) {
        this.memberSeq = userSeq;
        this.chatRoom = UUID.fromString(roomId);
    }
}
