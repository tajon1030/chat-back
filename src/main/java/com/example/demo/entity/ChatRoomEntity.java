package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Entity
@Getter
@Builder
@Table(name = "CHAT_ROOM")
public class ChatRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;

    public static ChatRoomEntity create(String name) {
        return ChatRoomEntity.builder()
                .name(name)
                .build();
    }
}
