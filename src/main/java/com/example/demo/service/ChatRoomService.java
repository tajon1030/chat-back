package com.example.demo.service;

import com.example.demo.config.page.PageCustom;
import com.example.demo.dto.ChatRoom;
import com.example.demo.entity.ChatRoomEntity;
import com.example.demo.repository.ChatMemberJoinRepository;
import com.example.demo.repository.ChatMemberRepository;
import com.example.demo.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatMemberJoinRepository chatMemberJoinRepository;

    @Transactional(readOnly = true)
    public PageCustom<ChatRoom> getChatRooms(Pageable pageable) {
        Page<ChatRoom> chatRoomPage = chatRoomRepository.findAll(pageable)
                .map(chatRoom -> {
                    chatMemberJoinRepository.countByRoomId(chatRoom.getId());
                    return ChatRoom.fromEntity(chatRoom, 0L);
                });

        return new PageCustom<>(chatRoomPage.getContent(), chatRoomPage.getPageable(), chatRoomPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ChatRoom getChatRoomById(String roomId) {
        return chatRoomRepository.findById(UUID.fromString(roomId))
                .map(chatRoom -> {
                    chatMemberJoinRepository.countByRoomId(chatRoom.getId());
                    return ChatRoom.fromEntity(chatRoom, 0L);
                })
                .orElseThrow(NoSuchElementException::new);
    }

    @Transactional
    public ChatRoom createChatRoom(String name) {
        ChatRoomEntity saved = chatRoomRepository.save(ChatRoomEntity.create(name));
        // TODO 생성과 동시에 enter한다고 봐야하지않을까
        return ChatRoom.fromEntity(saved, 1L);
    }

    @Transactional
    public ChatRoom enterChatRoom(String username, String roomId) {
        return null;
    }

    @Transactional
    public ChatRoom quitChatRoom(String username, String roomId) {

//        // 채팅방 인원수 -1
//        chatRoomRepository2.minusUserCount(roomId);
//        // 퇴장메시지를 채팅방에 발송
//        chatService.sendChatMessage(ChatMessage.builder()
//                .type(ChatMessage.MessageType.QUIT)
//                .roomId(roomId)
//                .sender(user.getUsername())
//                .build());
//        // 퇴장한 클라이언트id -room 매핑정보 삭제
//        chatRoomRepository2.removeUserEnterInfo(user.getUsername(), roomId);
//        chatRoomRepository2.findRoomById(roomId);

        return null;
    }

}
