package com.example.projectstudy.db.diet_inform.repos;

import com.example.projectstudy.db.diet_inform.entities.Diet_Inform_Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Diet_Inform_Likes_Repository extends JpaRepository<Diet_Inform_Likes, Long> {
    Optional<Diet_Inform_Likes> findByUserIdAndDietInformArticleId(Long userId, Long articleId);

    void deleteByDietInformArticleIdAndUserId(Long articleId, Long userId);

}
