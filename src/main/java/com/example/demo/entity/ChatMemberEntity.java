package com.example.demo.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "CHAT_MEMBER")
public class ChatMemberEntity {

    @EmbeddedId
    private ChatMemberPK chatMemberPK;

}
