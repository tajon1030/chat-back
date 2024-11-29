package com.example.demo.repository.chatRoom;

import com.example.demo.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, UUID>, ChatRoomCustomRepository {
}
