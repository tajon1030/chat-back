package com.example.demo.controller;

import com.example.demo.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final SimpMessageSendingOperations simpMessageSendingOperations;

    @MessageMapping("/chat/message")
    public void greeting(ChatMessage message) throws Exception {
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        }
        simpMessageSendingOperations.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

}
