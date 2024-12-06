package com.example.demo.service;

import com.example.demo.config.page.PageCustom;
import com.example.demo.dto.ChatMessage;
import com.example.demo.dto.ChatRoom;
import com.example.demo.entity.ChatMemberEntity;
import com.example.demo.entity.ChatMemberPK;
import com.example.demo.entity.ChatRoomEntity;
import com.example.demo.exception.CustomException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.ChatMemberRepository;
import com.example.demo.repository.UsersRepository;
import com.example.demo.repository.chatRoom.ChatRoomRepository;
import com.example.demo.security.Users;
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
    private final ChatService chatService;
    private final UsersRepository usersRepository;

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
     * 참여중인 채팅방 페이징 조회
     *
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PageCustom<ChatRoom> getEnteredChatRooms(Pageable pageable, Long userSeq) {
        Page<ChatRoom> chatRoomPage = chatRoomRepository.findEnteredChatRoomPage(userSeq, pageable);
        return new PageCustom<>(chatRoomPage.getContent(), chatRoomPage.getPageable(), chatRoomPage.getTotalElements());
    }


    /**
     * 채팅방 생성
     *
     * @param roomName
     * @return
     */
    @Transactional
    public ChatRoom createChatRoom(String roomName) {
        ChatRoomEntity saved = chatRoomRepository.save(ChatRoomEntity.create(roomName));
        return ChatRoom.fromEntity(saved, 0L);
    }

    /**
     * 채팅방 입장
     *
     * @param userSeq
     * @param roomId
     * @return
     */
    @Transactional
    public ChatRoom enterChatRoom(Long userSeq, String roomId) {
        ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(UUID.fromString(roomId))
                .map(room -> {
                    // 매핑 추가
                    ChatMemberEntity savedChatMember = chatMemberRepository.save(new ChatMemberEntity(userSeq, room));
                    return room.enter(savedChatMember);
                })
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        return ChatRoom.fromEntity(chatRoomEntity);
    }

    /**
     * 채팅방 나가기
     *
     * @param userSeq
     * @param roomId
     * @return
     */
    @Transactional
    public ChatRoom quitChatRoom(Long userSeq, String roomId) {
        // 파라미터 체크
        Users users = usersRepository.findById(userSeq)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(UUID.fromString(roomId))
                .map(room -> {
                    // 매핑 제거
                    ChatMemberEntity chatMemberEntity = new ChatMemberEntity(userSeq, room);
                    chatMemberRepository.delete(chatMemberEntity);
                    return room.exit(chatMemberEntity);
                })
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        // 퇴장메시지 발송
        chatService.sendChatMessage(ChatMessage.builder()
                .type(ChatMessage.MessageType.QUIT)
                .roomId(roomId)
                .sender(users.getUsername())
                .senderSeq(users.getSeq())
                .build());

        return ChatRoom.fromEntity(chatRoomEntity);
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
