package com.example.demo.repository;

import com.example.demo.dto.ChatRoom;
import jakarta.annotation.PostConstruct;
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
public class ChatRoomRepository {
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
     * 모든 채팅방 조회
     * @return
     */
    public List<ChatRoom> findAllRoom() {
        return opsHashChatRoom.values(CHAT_ROOMS);
    }

    /**
     * 특정 채팅방 조회
     * @param id
     * @return
     */
    public ChatRoom findRoomById(String id) {
        return opsHashChatRoom.get(CHAT_ROOMS, id);
    }

    /**
     * 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다.
     */
    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    /**
     * 입장한 채팅방id와 유저id 매핑정보 저장
     * @param userId
     * @param roomId
     */
    public void setUserEnterInfo(String userId, String roomId){
        opsHashEnterInfo.put(ENTER_INFO, userId, roomId);
    }

    public void isExistsUserEnterInfo(String userId, String roomId){

    }

    /**
     * userId - 채팅방id 매핑내역 삭제
     */
    public void removeUserEnterInfo(String userId, String roomId){
        opsHashEnterInfo.delete(ENTER_INFO, userId, roomId);
    }

    /**
     * 채팅방 인원수 조회
     * @param roomId
     * @return
     */
    public long getUserCount(String roomId){
        return Long.parseLong(Optional.ofNullable(opsValue.get(USER_COUNT+"_"+roomId)).orElse("0"));
    }

    /**
     * 입장 유저수 + 1
     * @param roomId
     * @return
     */
    public long plusUserCount(String roomId){
        return Optional.ofNullable(opsValue.increment(USER_COUNT+"_"+roomId)).orElse(0L);
    }

    /**
     * 입장 유저수 - 1
     * @param roomId
     * @return
     */
    public long minusUserCount(String roomId){
        return Optional.ofNullable(opsValue.decrement(USER_COUNT+"_"+roomId)).filter(count -> count > 0).orElse(0L);
    }

}
