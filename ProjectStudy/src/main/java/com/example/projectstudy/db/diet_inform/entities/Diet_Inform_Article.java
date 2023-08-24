package com.example.projectstudy.db.diet_inform.entities;

import com.example.projectstudy.db.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "DietInformArticle")
public class Diet_Inform_Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @NotBlank
    private String tag; // 태그

    @NotBlank
    private String title;
    @NotBlank
    private String content;

    private LocalDateTime created_at;

    @OneToMany(mappedBy = "dietInformArticle")
    private List<Diet_Inform_Comment> dietInformComments = new ArrayList<>();

    @OneToMany(mappedBy = "dietInformArticle")
    private List<Diet_Inform_Article_Img> dietArticleImgs = new ArrayList<>();

    @OneToMany(mappedBy = "dietInformArticle")
    private List<Diet_Inform_Likes> dietArticleLikes = new ArrayList<>();

}
