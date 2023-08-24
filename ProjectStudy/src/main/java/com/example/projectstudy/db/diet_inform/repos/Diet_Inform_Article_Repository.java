package com.example.projectstudy.db.diet_inform.repos;

import com.example.projectstudy.db.diet_inform.entities.Diet_Inform_Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Diet_Inform_Article_Repository extends JpaRepository<Diet_Inform_Article, Long> {
    // 작성자 이름으로 게시물 목록 가져오기
    Page<Diet_Inform_Article> findByUser_UsernameIgnoreCaseContaining(String username, Pageable pageable);

    // 태그로 게시물 목록 가져오기
    Page<Diet_Inform_Article> findByTagIgnoreCaseContaining(String tag, Pageable pageable);

    // 제목으로 게시물 목록 가져오기
    Page<Diet_Inform_Article> findByTitleIgnoreCaseContaining(String title, Pageable pageable);

}
