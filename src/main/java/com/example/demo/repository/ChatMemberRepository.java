package com.example.demo.repository;

import com.example.demo.entity.ChatMemberEntity;
import com.example.demo.entity.ChatMemberPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMemberRepository extends JpaRepository<ChatMemberEntity, ChatMemberPK> {
}
