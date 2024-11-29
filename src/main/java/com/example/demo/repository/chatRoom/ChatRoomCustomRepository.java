package com.example.demo.repository.chatRoom;

import com.example.demo.dto.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ChatRoomCustomRepository {
    Page<ChatRoom> findChatRoomPage(Pageable pageable);

    Optional<ChatRoom> findChatRoomById(UUID id);

}
