package com.example.projectstudy.db.diet_inform.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="DietInformArticleImg")
public class Diet_Inform_Article_Img{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "diet_inform_article_id")
    private Diet_Inform_Article dietInformArticle;

    private String imgUrl;
}