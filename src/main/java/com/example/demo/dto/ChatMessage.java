package com.example.demo.dto;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="chat_message")
public class ChatMessage {

    @Id
    private String id;

    // 메시지 타입 : 입장, 채팅
    public enum MessageType {
        ENTER, TALK, QUIT,
    }

    private MessageType type; // 메시지 타입
    // 채팅방 정보
    private String roomId; // 방번호
    // XXX 이름을 분리해야하나?
    private String sender; // 메시지 보낸사람
    private Long senderSeq; // 메시지 보낸사람(pk)
    private String message; // 메시지

    @Builder.Default
    private LocalDateTime sendDt = LocalDateTime.now();
}