package com.example.demo.service;

import com.example.demo.dto.ChatMessage;
import com.example.demo.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final RabbitTemplate rabbitTemplate;
    @Value("${spring.rabbitmq.exchange}")
    private String EXCHANGE_NAME; // chat.exchange

    /**
     * destination에서 room정보추출
     *
     * @param destination
     * @return
     */
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf("/");
        if (lastIndex != -1) {
            return destination.substring(lastIndex + 1);
        }
        return "";
    }

    /**
     * 메시지 발송
     *
     * @param chatMessage
     */
    @Transactional
    public void sendChatMessage(ChatMessage chatMessage) {
        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에 입장했습니다.");
            chatMessage.setSender("[알림]");
        } else if (ChatMessage.MessageType.QUIT.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에서 나갔습니다.");
            chatMessage.setSender("[알림]");
        }
        // rabbitmq로 메시지 발행
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "room." + chatMessage.getRoomId(), chatMessage);
        // 메시지 저장
        chatMessageRepository.save(chatMessage);
    }

    /**
     * 전체 메시지 조회
     *
     * @param roomId 채팅방 id
     * @return
     */
    public List<ChatMessage> getChatMessages(String roomId) {
        // TODO 페이징 처리 필요
        return chatMessageRepository.findAllChatMessageByRoomId(roomId);
    }
}
