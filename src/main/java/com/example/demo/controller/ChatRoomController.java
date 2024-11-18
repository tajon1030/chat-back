package com.example.demo.controller;

import com.example.demo.dto.ChatRoom;
import com.example.demo.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;

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
}
