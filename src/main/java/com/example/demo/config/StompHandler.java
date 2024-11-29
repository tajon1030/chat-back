package com.example.demo.config;

import com.example.demo.dto.ChatMessage;
import com.example.demo.repository.ChatRoomRepository2;
import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.security.UserDetailsImpl;
import com.example.demo.service.ChatRoomService;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatService chatService;
    private final ChatRoomService chatRoomService;
    private final CustomUserDetailsService userDetailsService;
    private final ChatRoomRepository2 chatRoomRepository2;


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
            // header에서 구독 destination정보를로 roomId 추출
            String roomId = chatService.getRoomId(
                    Optional.ofNullable((String) message.getHeaders().get("simpDestination"))
                            .orElse("InvalidRoomId"));
            // 사용자 인증 정보 설정
            UserDetails userDetails = userDetailsService.loadUserByUsername(jwtTokenProvider.getUsername(accessor.getFirstNativeHeader("Authorization")));
            if (userDetails instanceof UserDetailsImpl customUserDetails) {
                Long userSeq = customUserDetails.getId();

                // 새로입장하는 유저일 경우 인원 수 증가 및 입장메시지 발송
                if (chatRoomService.isExistsChatMember(userSeq, roomId)) {
                    // 매핑 추가
                    // 인원수 증가
                    // 입장메시지 발송
                    String name = jwtTokenProvider.getAuthentication(accessor.getFirstNativeHeader("Authorization")).getName();
                    chatService.sendChatMessage(ChatMessage.builder()
                            .type(ChatMessage.MessageType.ENTER)
                            .roomId(roomId)
                            .sender(name)
                            .build());
                }
            }
        }
        return message;
    }

}