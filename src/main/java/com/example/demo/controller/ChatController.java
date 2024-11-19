package com.example.demo.controller;

import com.example.demo.dto.ChatMessage;
import com.example.demo.repository.ChatRoomRepository;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final JwtTokenProvider provider;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void greeting(ChatMessage message, @Header("Authorization") String token) throws Exception {
        String id = provider.getUsername(token);
        // 로그인 회원 정보로 대화명 설정
        message.setSender(id);
        // 채팅방 인원수 세팅
        message.setUserCount(chatRoomRepository.getUserCount(message.getRoomId()));
        // Websocket에 발행된 메시지를 redis로 발행
        chatService.sendChatMassage(message);
    }

}
