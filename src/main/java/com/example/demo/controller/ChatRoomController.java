package com.example.demo.controller;

import com.example.demo.dto.ChatRoom;
import com.example.demo.repository.ChatRoomRepository;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.security.LoginInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;
    private final JwtTokenProvider provider;

    /**
     * 전체 채팅방 목록 반환
     *
     * @return
     */
    @GetMapping("/rooms")
    public List<ChatRoom> room() {
        return chatRoomRepository.findAllRoom();
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

    @GetMapping("/user")
    public LoginInfo getUserInfo(){
        // 로그인회원 정보를 가져와서 id를 token으로 생성해 LoginInfo로 전달해줌
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        return LoginInfo.builder().name(name).token(provider.generateToken(name)).build();
    }
}
