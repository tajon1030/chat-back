package com.example.demo.controller.chat;

import com.example.demo.dto.ChatMessage;
import com.example.demo.repository.ChatRoomRepository2;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final JwtTokenProvider provider;
    private final ChatRoomRepository2 chatRoomRepository2;
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void greeting(ChatMessage message, @Header("Authorization") String token) throws Exception {
        String id = provider.getUsername(token);
        // 로그인 회원 정보로 대화명 설정
        message.setSender(id);
        // 채팅방 인원수 세팅
        message.setUserCount(chatRoomRepository2.getUserCount(message.getRoomId()));
        // Websocket에 발행된 메시지를 redis로 발행
        chatService.sendChatMessage(message);
    }

    @ResponseBody
    @GetMapping("/chat/room/{roomId}/messages")
    public ResponseEntity<?> getChatMessages(@PathVariable String roomId) {
        // TODO 내가 참여한 채팅방인지 검증 필요
        return ResponseEntity.ok()
                .body(chatService.getChatMessages(roomId));
    }

}
