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

    public static ChatRoom fromEntity(ChatRoomEntity entity, long userCount) {
        return ChatRoom.builder()
                .roomId(entity.getId())
                .name(entity.getName())
                .userCount(userCount)
                .build();
    }
    public ChatRoom(String roomId, String name, long userCount) {
        this.roomId = UUID.fromString(roomId);
        this.name = name;
        this.userCount = userCount;
    }

}
