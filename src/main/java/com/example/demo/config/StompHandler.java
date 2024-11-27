package com.example.demo.config;

import com.example.demo.dto.ChatMessage;
import com.example.demo.repository.ChatRoomRepository;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatService chatService;
    private final ChatRoomRepository chatRoomRepository;


    // websocket을 통해 들어온 요청이 처리 되기전 실행
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            // 사용자 인증 정보 설정
            Authentication authentication = jwtTokenProvider.getAuthentication(accessor.getFirstNativeHeader("Authorization"));
            if (!jwtTokenProvider.validateToken(accessor.getFirstNativeHeader("Authorization"))) {
                throw new RuntimeException("token expired");
            }
            // 사용자 인증 정보 설정
            accessor.setUser(authentication);
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            // TODO 새입장유저 - 기존유저 확인해서 새입장유저일경우에만 인원수 증가시키고 입장메시지 발송하도록 한다.
            // 사용자 인증 정보 설정
            Authentication authentication = jwtTokenProvider.getAuthentication(accessor.getFirstNativeHeader("Authorization"));
            // header에서 구독 destination정보를 얻고 roomId 추출
            String roomId = chatService.getRoomId(
                    Optional.ofNullable((String) message.getHeaders().get("simpDestination"))
                            .orElse("InvalidRoomId"));

            // 채팅방에 들어온 클라이언트 유저id와 roomId 매핑
            chatRoomRepository.setUserEnterInfo(authentication.getName(), roomId);
            // 채팅방인원수 +1
            chatRoomRepository.plusUserCount(roomId);
            // 클라이언트 입장메시지를 채팅방에 발송
            String name = jwtTokenProvider.getAuthentication(accessor.getFirstNativeHeader("Authorization")).getName();
            chatService.sendChatMessage(ChatMessage.builder()
                    .type(ChatMessage.MessageType.ENTER)
                    .roomId(roomId)
                    .sender(name)
                    .build());
        }
        return message;
    }

}