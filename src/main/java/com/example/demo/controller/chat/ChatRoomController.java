package com.example.demo.controller.chat;

import com.example.demo.config.page.PageCustom;
import com.example.demo.controller.ApiResult;
import com.example.demo.dto.ChatRoom;
import com.example.demo.security.UserDetailsImpl;
import com.example.demo.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

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
        return ApiResult.OK(chatRoomService.createChatRoom(roomName));
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
    public ApiResult<ChatRoom> roomInfo(@PathVariable String roomId, @AuthenticationPrincipal UserDetailsImpl user) {
        return ApiResult.OK(chatRoomService.quitChatRoom(user.getSeq(), roomId));
    }
}
