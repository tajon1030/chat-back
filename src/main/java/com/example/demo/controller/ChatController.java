package com.example.demo.controller;

import com.example.demo.dto.ChatMessage;
import com.example.demo.repository.ChatRoomRepository;
import com.example.demo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final JwtTokenProvider provider;
    private final RedisTemplate<String,Object> redisTemplate;
    private final ChannelTopic channelTopic;

    @MessageMapping("/chat/message")
    public void greeting(ChatMessage message, @Header("token") String token) throws Exception {
        String id = provider.getClaims(token)
                .getId();
        // 로그인 회원 정보로 대화명 설정
        message.setSender(id);
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            message.setSender("[알림]");
            message.setMessage(id + "님이 입장하셨습니다.");
        }
        // Websocket에 발행된 메시지를 redis로 발행
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }

}
