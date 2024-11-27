package com.example.demo.controller;

import com.example.demo.dto.ChatMessage;
import com.example.demo.dto.ChatRoom;
import com.example.demo.repository.ChatRoomRepository;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.security.Users;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    /**
     * 전체 채팅방 목록 반환
     *
     * @return
     */
    @GetMapping("/rooms")
    public List<ChatRoom> room() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllRoom();
        chatRooms.forEach(room -> room.setUserCount(chatRoomRepository.getUserCount(room.getRoomId())));
        return  chatRooms;
    }

    /**
     * 채팅방 생성
     *
     * @param name 채팅방명
     * @return
     */
    @PostMapping("/room")
    public ChatRoom createRoom(@RequestParam String name) {
        return chatRoomRepository.createChatRoom(name);
    }

    /**
     * 특정 채팅방 조회
     *
     * @param roomId 채팅방 id
     * @return
     */
    @GetMapping("/room/{roomId}")
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return chatRoomRepository.findRoomById(roomId);
    }

    /**
     * 채팅방 퇴장
     *
     * @param roomId 채팅방 id
     * @return
     */
    @PostMapping("/room/{roomId}/quit")
    public ChatRoom roomInfo(@PathVariable String roomId, @AuthenticationPrincipal User user) {

        // 채팅방 인원수 -1
        chatRoomRepository.minusUserCount(roomId);
        // 퇴장메시지를 채팅방에 발송
        chatService.sendChatMessage(ChatMessage.builder()
                .type(ChatMessage.MessageType.QUIT)
                .roomId(roomId)
                .sender(user.getUsername())
                .build());
        // 퇴장한 클라이언트id -room 매핑정보 삭제
        chatRoomRepository.removeUserEnterInfo(user.getUsername(), roomId);
        return chatRoomRepository.findRoomById(roomId);
    }
}
