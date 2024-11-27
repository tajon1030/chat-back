package com.example.demo.service;

import com.example.demo.dto.ChatMessage;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.ChatRoomRepository2;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ChatService {
    
    private final ChatRoomRepository2 chatRoomRepository2;
    private final ChatMessageRepository chatMessageRepository;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;

    /**
     * destination에서 room정보추출
     * @param destination
     * @return
     */
    public String getRoomId(String destination){
        int lastIndex = destination.lastIndexOf("/");
        if( lastIndex != -1) {
            return destination.substring(lastIndex + 1);
        }
        return "";
    }

    /**
     * 메시지 발송
     * @param chatMessage
     */
    public void sendChatMessage(ChatMessage chatMessage){
        chatMessage.setUserCount(chatRoomRepository2.getUserCount(chatMessage.getRoomId()));
        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에 입장했습니다.");
            chatMessage.setSender("[알림]");
        } else if (ChatMessage.MessageType.QUIT.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에서 나갔습니다.");
            chatMessage.setSender("[알림]");
        }
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);

        // 메시지 저장
        chatMessageRepository.save(chatMessage);
    }

    /**
     * 전체 메시지 조회
     *
     * @param roomId 채팅방 id
     * @return
     */
    public List<ChatMessage> getChatMessages(String roomId){
        // TODO 페이징 처리 필요
        return chatMessageRepository.findAllChatMessageByRoomId(roomId);
    }
}
