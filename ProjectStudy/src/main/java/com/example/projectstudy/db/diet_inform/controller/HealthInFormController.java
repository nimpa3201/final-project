package com.example.projectstudy.db.diet_inform.controller;

import com.example.projectstudy.db.UserEntity;
import com.example.projectstudy.db.UserRepository;
import com.example.projectstudy.db.diet_inform.dto.Diet_Inform_Post_Dto;
import com.example.projectstudy.db.diet_inform.entities.Diet_Inform_Article;
import com.example.projectstudy.db.diet_inform.entities.Diet_Inform_Article_Img;
import com.example.projectstudy.db.diet_inform.repos.Diet_Inform_Article_Img_Repository;
import com.example.projectstudy.db.diet_inform.repos.Diet_Inform_Article_Repository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/healthInform")
public class HealthInFormController {

    private final Diet_Inform_Article_Repository dietInformArticleRepository;
    private final Diet_Inform_Article_Img_Repository dietInformArticleImgRepository;
    private final UserRepository userRepository;

    // 헬스 정보 공유 게시판 메인

    @GetMapping("/main")
    public String healthInformMain(){

        return "healthMain";
    }

    // 다이어트 게시판
    @GetMapping("/diet")
    public String dietInform(){

        return "DietInform";
    }

    // 다이어트 게시판 글 작성
    @GetMapping("/diet/post")
    public String dietInformPost(){

        return "DietInformPost";
    }

    @PostMapping("/diet/post")
    public ResponseEntity<String> dietInformPostStart(@RequestBody @Valid Diet_Inform_Post_Dto dto, LocalDateTime localDateTime){

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        log.info(username);
        log.info("다이어트 게시판 글 작성을 시작합니다");

        Optional<UserEntity> user = userRepository.findByUsername(username);

        Diet_Inform_Article diet_inform_article = new Diet_Inform_Article();
        diet_inform_article.setTitle(dto.getTitle());
        diet_inform_article.setContent(dto.getContent());
        diet_inform_article.setTag(dto.getTag());
        diet_inform_article.setUser(user.get());
        diet_inform_article.setCreated_at(localDateTime.now());

        try {
            dietInformArticleRepository.save(diet_inform_article);
            log.info("작성완료");

            Long generatedId = diet_inform_article.getId();

            String Id = generatedId.toString();

            return ResponseEntity.ok(Id);
        } catch (Exception e) {
            log.error("글 작성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("글 작성에 실패하였습니다.");
        }
    }

    // 이미지 업로드
    @PostMapping("/diet/postImages/{id}")
    public ResponseEntity<String> dietInformPostImage(@PathVariable String id, @RequestParam("images") MultipartFile[] images){

        Long Id = Long.parseLong(id);

        if(images.length == 0){
            return ResponseEntity.ok("No Images");
        }
        try {
            saveImages(images, Id);
            return ResponseEntity.ok("Images uploaded successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload images.");
        }
    }
    public void saveImages(MultipartFile[] images, Long id){
        Optional<Diet_Inform_Article> diet_inform_article = dietInformArticleRepository.findById(id);

        if(diet_inform_article.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Diet_Inform_Article dietInformArticle = diet_inform_article.get();

        for (MultipartFile image : images){
            if (!image.isEmpty()){
                try
                {
                    // 1. 이미지 저장 경로 설정 및 폴더 생성
                    String profileDir = String.format("media/diet/%d/", id);
                    Files.createDirectories(Path.of(profileDir));
                    // 2. 이미지 파일 이름 만들기 (원래 파일 이름 그대로 사용)
                    String originalFilename = image.getOriginalFilename();
                    // 3. 폴더와 파일 경로를 포함한 이름 만들기
                    String profilePath = profileDir + originalFilename;
                    // 4. MultipartFile 을 저장하기
                    image.transferTo(Path.of(profilePath));

                    // 5. 데이터베이스 업데이트
                    Diet_Inform_Article_Img ImagesEntity = new Diet_Inform_Article_Img();
                    ImagesEntity.setDiet_inform_article(dietInformArticle);
                    ImagesEntity.setImgUrl(String.format("/static/article/%d/%s", id, originalFilename));
                    dietInformArticleImgRepository.save(ImagesEntity);

                }
                catch (IOException e) {
                    e.printStackTrace();
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save image.");
                }

            }
        }
    }




}
