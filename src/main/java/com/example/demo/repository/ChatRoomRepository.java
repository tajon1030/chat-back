package com.example.demo.repository;

import com.example.demo.entity.ChatRoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, UUID> {
    Page<ChatRoomEntity> findAll(Pageable pageable);
}
