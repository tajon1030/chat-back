package com.example.demo.dto;

import com.example.demo.entity.ChatRoomEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ChatRoom {

    private UUID roomId;
    private String name;
    private long userCount;

    public static ChatRoom fromEntity(ChatRoomEntity entity, long userCount) {
        return ChatRoom.builder()
                .roomId(entity.getId())
                .name(entity.getName())
                .userCount(userCount)
                .build();
    }

}
