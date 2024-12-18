package com.example.chat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.chat.models.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
