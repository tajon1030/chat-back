package com.example.demo.dto;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="chat_message")
public class ChatMessage {

    //데이터 저장 시 id를 넣지 않고 다른 필드들만으로 데이터베이스에 저장하여 id를 자동 생성하기 위해서 @Id를 달고 String 혹은 ObjectId 타입으로 id를 추가한다.
    @Id
    private String id;

    // 메시지 타입 : 입장, 채팅
    public enum MessageType {
        ENTER, TALK, QUIT,
    }

    private MessageType type; // 메시지 타입
    private String roomId; // 방번호
    private String sender; // 메시지 보낸사람
    private String message; // 메시지
    private long userCount; // 채팅방 인원수
}