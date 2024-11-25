package com.example.demo.repository;

import com.example.demo.dto.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findAllChatMessageByRoomId(String roomId);
}
