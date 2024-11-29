package com.example.demo.repository;

import com.example.demo.dto.ChatRoom;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepository2 {
    // Redis
    private static final String CHAT_ROOMS = "CHAT_ROOM"; // 채팅룸 저장
    private static final String USER_COUNT = "USER_COUNT"; // 채팅룸 입장 클라이언트수 저장
    private static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸 입장 클라이언트 userId - 채팅룸id 매핑저장
    private final RedisTemplate<String, Object> redisTemplate;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> opsHashEnterInfo;
    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> opsValue;



    /**
     * 채팅방 인원수 조회
     * @param roomId
     * @return
     */
    public long getUserCount(String roomId){
        return Long.parseLong(Optional.ofNullable(opsValue.get(USER_COUNT+"_"+roomId)).orElse("0"));
    }

}
