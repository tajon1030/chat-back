package com.example.demo.controller.chat;

import com.example.demo.dto.ChatMessage;
import com.example.demo.exception.CustomException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.security.UserDetailsImpl;
import com.example.demo.service.ChatRoomService;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final JwtTokenProvider provider;
    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    @MessageMapping("chat.message")
    public void greeting(ChatMessage message, @Header("Authorization") String token) {
        // 로그인 회원 정보로 대화명 설정
        message.setSender(provider.getUsername(token));
        message.setSenderSeq(provider.getSeq(token));
        // Websocket에 발행된 메시지를 발행
        chatService.sendChatMessage(message);
    }

    /**
     * 참여 채팅방 이전 대화내용 조회
     *
     * @param roomId
     * @param user
     * @return
     */
    @ResponseBody
    @GetMapping("/chat/room/{roomId}/messages")
    public ResponseEntity<?> getChatMessages(@PathVariable String roomId, @AuthenticationPrincipal UserDetailsImpl user) {
        // 내가 참여한 채팅방인지 검증
        if(!chatRoomService.isExistsChatMember(user.getSeq(), roomId)){
           throw new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND, "참여한 채팅방이 아닙니다.");
        }
        return ResponseEntity.ok()
                .body(chatService.getChatMessages(roomId));
    }



    //receive()는 단순히 큐에 들어온 메세지를 소비만 한다. (현재는 디버그용도)
    @RabbitListener(queues = "chat.queue")
    public void receive(ChatMessage message){
        log.info("received : " + message.getMessage());
    }
}
