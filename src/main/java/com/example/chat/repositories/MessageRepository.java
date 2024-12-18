package com.example.chat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.chat.models.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRoomId(Long roomId);
}
