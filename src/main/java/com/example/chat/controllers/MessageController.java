package com.example.chat.controllers;


import com.example.chat.dtos.MessageDTO;
import com.example.chat.models.Message;
import com.example.chat.repositories.MessageRepository;
import com.example.chat.repositories.RoomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;

    public MessageController(MessageRepository messageRepository, RoomRepository roomRepository) {
        this.messageRepository = messageRepository;
        this.roomRepository = roomRepository;
    }

    // Get messages for a specific room
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<MessageDTO>> getMessagesByRoom(@PathVariable Long roomId) {
        return roomRepository.findById(roomId)
                .map(room -> {
                    List<Message> messages = messageRepository.findByRoomId(roomId);
                    List<MessageDTO> messageDTOs = messages.stream()
                            .map(this::convertToDTO)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(messageDTOs);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new message
    @PostMapping
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        if (!roomRepository.existsById(message.getRoom().getId())) {
            return ResponseEntity.badRequest().body(null);
        }

        Message savedMessage = messageRepository.save(message);
        return ResponseEntity.ok(savedMessage);
    }

    // Update a message by ID
    @PutMapping("/{id}")
    public ResponseEntity<Message> updateMessage(@PathVariable Long id, @RequestBody Message updatedMessage) {
        return messageRepository.findById(id)
                .map(existingMessage -> {
                    existingMessage.setContent(updatedMessage.getContent());
                    existingMessage.setType(updatedMessage.getType());
                    existingMessage.setFileUrl(updatedMessage.getFileUrl());
                    Message savedMessage = messageRepository.save(existingMessage);
                    return ResponseEntity.ok(savedMessage);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete a message by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        if (messageRepository.existsById(id)) {
            messageRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }


    // Helper method to convert Message to MessageDTO
    private MessageDTO convertToDTO(Message message) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setSender(message.getSender());
        messageDTO.setContent(message.getContent());
        messageDTO.setFileUrl(message.getFileUrl());
        messageDTO.setType(message.getType());
        messageDTO.setCreatedAt(message.getCreatedAt());
        return messageDTO;
    }
}
