package com.example.chat.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MessageDTO {
    private Long id;
    private Long sender;
    private String content;
    private String fileUrl;
    private String type;
    private LocalDateTime createdAt;
}
