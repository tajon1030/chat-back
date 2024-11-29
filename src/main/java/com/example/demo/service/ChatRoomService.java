package com.example.demo.service;

import com.example.demo.config.page.PageCustom;
import com.example.demo.dto.ChatRoom;
import com.example.demo.entity.ChatMemberPK;
import com.example.demo.entity.ChatRoomEntity;
import com.example.demo.repository.ChatMemberJoinRepository;
import com.example.demo.repository.ChatMemberRepository;
import com.example.demo.repository.chatRoom.ChatRoomRepository;
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

    /**
     * 채팅방 페이징 조회
     *
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PageCustom<ChatRoom> getChatRooms(Pageable pageable) {
        Page<ChatRoom> chatRoomPage = chatRoomRepository.findChatRoomPage(pageable);
        return new PageCustom<>(chatRoomPage.getContent(), chatRoomPage.getPageable(), chatRoomPage.getTotalElements());
    }

    /**
     * 채팅방 단건 조회
     *
     * @param roomId
     * @return
     */
    @Transactional(readOnly = true)
    public ChatRoom getChatRoomById(String roomId) {
        return chatRoomRepository.findChatRoomById(UUID.fromString(roomId))
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * 채팅방 생성
     *
     * @param userId
     * @param roomName
     * @return
     */
    @Transactional
    public ChatRoom createChatRoom(String userId, String roomName) {
        ChatRoomEntity saved = chatRoomRepository.save(ChatRoomEntity.create(roomName));
        return ChatRoom.fromEntity(saved, 0L);
    }

    @Transactional
    public ChatRoom enterChatRoom(String userId, String roomId) {
        // 채팅방 인원 증가
        return null;
    }

    @Transactional
    public ChatRoom quitChatRoom(String userId, String roomId) {
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

    /**
     * 채팅방 참여 멤버인지 확인
     *
     * @param userSeq
     * @param roomId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isExistsChatMember(Long userSeq, String roomId) {
        return chatMemberRepository.existsById(new ChatMemberPK(userSeq, roomId));
    }

}
