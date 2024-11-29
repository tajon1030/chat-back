package com.example.demo.controller.chat;

import com.example.demo.config.page.PageCustom;
import com.example.demo.controller.ApiResult;
import com.example.demo.dto.ChatMessage;
import com.example.demo.dto.ChatRoom;
import com.example.demo.repository.ChatRoomRepository2;
import com.example.demo.security.UserDetailsImpl;
import com.example.demo.security.Users;
import com.example.demo.service.ChatRoomService;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomRepository2 chatRoomRepository2;
    private final ChatService chatService;
    private final ChatRoomService chatRoomService;

    /**
     * 전체 채팅방 목록 반환
     *
     * @return
     */
    @GetMapping("/rooms")
    public ApiResult<PageCustom<ChatRoom>> room(@SortDefault.SortDefaults({
            @SortDefault(sort="createdDt", direction = Sort.Direction.DESC)
    }) Pageable pageable) {
        PageCustom<ChatRoom> chatRoomsResponse = chatRoomService.getChatRooms(pageable);
        return ApiResult.OK(chatRoomsResponse);
    }

    /**
     * 채팅방 생성
     *
     * @param roomName 채팅방명
     * @return
     */
    @PostMapping("/room")
    public ApiResult<ChatRoom> createRoom(@RequestParam(name="name") String roomName, @AuthenticationPrincipal UserDetailsImpl user) {
        return ApiResult.OK(chatRoomService.createChatRoom(user.getUsername(), roomName));
    }

    /**
     * 특정 채팅방 조회
     *
     * @param roomId 채팅방 id
     * @return
     */
    @GetMapping("/room/{roomId}")
    public ApiResult<ChatRoom> roomInfo(@PathVariable String roomId) {
        return ApiResult.OK(chatRoomService.getChatRoomById(roomId));
    }

    /**
     * 채팅방 퇴장
     *
     * @param roomId 채팅방 id
     * @return
     */
    @PostMapping("/room/{roomId}/quit")
    public ApiResult<ChatRoom> roomInfo(@PathVariable String roomId, @AuthenticationPrincipal User user) {
        return ApiResult.OK(chatRoomService.quitChatRoom(user.getUsername(), roomId));
    }
}
