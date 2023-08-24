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
    @JoinColumn(name = "diet_inform_article_id")
    private Diet_Inform_Article dietInformArticle;

    private String content;

    private LocalDateTime createdAt;
}
