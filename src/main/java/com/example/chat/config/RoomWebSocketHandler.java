package com.example.chat.config;


import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.example.chat.dtos.MessageDTO;
import com.example.chat.models.Message;
import com.example.chat.models.Room;
import com.example.chat.repositories.MessageRepository;
import com.example.chat.repositories.RoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RoomWebSocketHandler implements WebSocketHandler {

    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper;

    // Map to keep track of room connections
    private final ConcurrentHashMap<String, ConcurrentHashMap<WebSocketSession, Boolean>> rooms = new ConcurrentHashMap<>();

    public RoomWebSocketHandler(RoomRepository roomRepository, MessageRepository messageRepository, ObjectMapper objectMapper) {
        this.roomRepository = roomRepository;
        this.messageRepository = messageRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        String roomId = getRoomId(session);
        rooms.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(session, true);
    }

    @Override
    public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) throws Exception {
        String roomId = getRoomId(session);
        String payload = message.getPayload().toString();

        // Parse incoming message
        MessageDTO chatMessage = objectMapper.readValue(payload, MessageDTO.class);

        // Save the message to the database
        Room room = roomRepository.findById(Long.valueOf(roomId)).orElseThrow(() -> new IllegalArgumentException("Invalid Room"));
        Message dbMessage = new Message();
        dbMessage.setContent(chatMessage.getContent());
        dbMessage.setSender(chatMessage.getSender());
        dbMessage.setFileUrl(chatMessage.getFileUrl());
        dbMessage.setType(chatMessage.getType());
        dbMessage.setRoom(room);

        Message savedMessage = messageRepository.save(dbMessage);
        chatMessage.setCreatedAt(savedMessage.getCreatedAt());
        chatMessage.setId(savedMessage.getId());

        // Broadcast message to all room members
        for (WebSocketSession s : rooms.get(roomId).keySet()) {
            s.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
        }
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
        session.close();
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        String roomId = getRoomId(session);
        rooms.getOrDefault(roomId, new ConcurrentHashMap<>()).remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private String getRoomId(@NonNull WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) {
            throw new IllegalArgumentException("WebSocket session URI is null. Unable to extract roomId.");
        }

        String path = uri.getPath();
        if (path == null || !path.startsWith("/room/")) {
            throw new IllegalArgumentException("Invalid WebSocket path: " + path);
        }

        String[] pathSegments = path.split("/");
        if (pathSegments.length < 3) {
            throw new IllegalArgumentException("Path does not contain a roomId: " + path);
        }

            return pathSegments[2];
        }

}
