package com.example.projectstudy.chat.jpa;

import com.example.projectstudy.db.diet_inform.entities.Diet_Inform_Article;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "chat_room")
@Data
public class ChatRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roomName;

    @OneToOne
    @JoinColumn(name = "diet_inform_article_id")
    private Diet_Inform_Article dietInformArticle;
}
