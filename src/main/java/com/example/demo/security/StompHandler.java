package com.example.demo.security;

import com.example.demo.dto.ChatMessage;
import com.example.demo.repository.ChatRoomRepository;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;
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
            if (!jwtTokenProvider.validateToken(accessor.getFirstNativeHeader("Authorization"))) {
                throw new RuntimeException("token expired");
            }
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            // header에서 구독 destination정보를 얻고 roomId 추출
            String roomId = chatService.getRoomId(
                    Optional.ofNullable((String) message.getHeaders().get("simpDestination"))
                            .orElse("InvalidRoomId"));
            // 채팅방에 들어온 클라이언트 sessionId와 roomId 매핑
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            chatRoomRepository.setUserEnterInfo(sessionId, roomId);
            // 채팅방인원수 +1
            chatRoomRepository.plusUserCount(roomId);
            // 클라이언트 입장메시지를 채팅방에 발송
            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser"))
                    .map(Principal::getName)
                    .orElse("UnknownUser");
            chatService.sendChatMassage(ChatMessage.builder()
                    .type(ChatMessage.MessageType.ENTER)
                    .roomId(roomId)
                    .sender(name)
                    .build());
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            // 연결이 종료된 클라이언트 sessionId로 채팅방id를 얻는다
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = chatRoomRepository.getUserEnterRoomId(sessionId);
            // 채팅방 인원수 -1
            chatRoomRepository.minusUserCount(roomId);
            // 퇴장메시지를 채팅방에 발송
            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser"))
                    .map(Principal::getName)
                    .orElse("UnknownUser");
            chatService.sendChatMassage(ChatMessage.builder()
                    .type(ChatMessage.MessageType.QUIT)
                    .roomId(roomId)
                    .sender(name)
                    .build());
            // 퇴장한 클라이언트-room 매핑정보 삭제
            chatRoomRepository.removeUserEnterInfo(sessionId);
        }
        return message;
    }

}