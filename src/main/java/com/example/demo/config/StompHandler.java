package com.example.demo.config;

import com.example.demo.dto.ChatMessage;
import com.example.demo.exception.CustomException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.ChatRoomService;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatService chatService;
    private final ChatRoomService chatRoomService;


    // websocket을 통해 들어온 요청이 처리 되기전 실행
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            if (!jwtTokenProvider.validateToken(accessor.getFirstNativeHeader("Authorization"))) {
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            String roomId = chatService.getRoomId(
                    Optional.ofNullable((String) message.getHeaders().get("simpDestination"))
                            .orElse("InvalidRoomId"));
            String token = accessor.getFirstNativeHeader("Authorization");
            // 새로입장하는 유저일 경우 인원 수 증가 및 입장메시지 발송
            if (!chatRoomService.isExistsChatMember(jwtTokenProvider.getSeq(token), roomId)) {
                // 매핑 추가 및 인원수 증가
                chatRoomService.enterChatRoom(jwtTokenProvider.getSeq(token), roomId);
                // 입장메시지 발송
                chatService.sendChatMessage(ChatMessage.builder()
                        .type(ChatMessage.MessageType.ENTER)
                        .roomId(roomId)
                        .sender(jwtTokenProvider.getUsername(token))
                        .senderSeq(jwtTokenProvider.getSeq(token))
                        .build());
            }
        }
        return message;
    }

}