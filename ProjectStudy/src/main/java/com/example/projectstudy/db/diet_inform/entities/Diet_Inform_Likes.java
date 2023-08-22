package com.example.projectstudy.db.diet_inform.entities;

import com.example.projectstudy.db.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "diet_inform_likes")
public class Diet_Inform_Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "diet_inform_article_id")
    private Diet_Inform_Article diet_inform_article;
}
