package com.example.projectstudy.db.diet_inform.entities;

import com.example.projectstudy.db.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "DietInformComment")
public class Diet_Inform_Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "DietInformArticleId")
    private Diet_Inform_Article diet_inform_article;

    private String content;

    private LocalDateTime createdAt;
}
