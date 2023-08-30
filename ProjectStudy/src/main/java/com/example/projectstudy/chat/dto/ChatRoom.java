package com.example.projectstudy.chat.dto;

import com.example.projectstudy.chat.jpa.ChatRoomEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    private Long id;
    private String roomName;

    public static ChatRoom fromEntity(ChatRoomEntity entity) {
        return new ChatRoom(
                entity.getId(),
                entity.getRoomName()
        );
    }
}