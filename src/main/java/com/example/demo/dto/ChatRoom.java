package com.example.demo.dto;

import com.example.demo.entity.ChatRoomEntity;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoom {

    private UUID roomId;
    private String name;
    private long userCount;
    private boolean isEntered;

    public static ChatRoom fromEntity(ChatRoomEntity entity) {
        return ChatRoom.builder()
                .roomId(entity.getId())
                .name(entity.getName())
                .build();
    }

    public static ChatRoom fromEntity(ChatRoomEntity entity, long userCount) {
        return ChatRoom.builder()
                .roomId(entity.getId())
                .name(entity.getName())
                .userCount(userCount)
                .build();
    }

    public static ChatRoom fromEntity(ChatRoomEntity entity, long userCount, boolean isEntered) {
        return ChatRoom.builder()
                .roomId(entity.getId())
                .name(entity.getName())
                .userCount(userCount)
                .isEntered(isEntered)
                .build();
    }

}
