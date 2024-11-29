package com.example.demo.entity;

import com.example.demo.entity.base.MutableBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CHAT_ROOM")
public class ChatRoomEntity extends MutableBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;

    @OneToMany(mappedBy = "chatRoom")
    private List<ChatMemberEntity> chatMember;

    public static ChatRoomEntity create(String name) {
        return ChatRoomEntity.builder()
                .name(name)
                .build();
    }
}
