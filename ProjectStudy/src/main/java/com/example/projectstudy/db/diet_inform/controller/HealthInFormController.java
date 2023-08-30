package com.example.projectstudy.db.diet_inform.controller;

import com.example.projectstudy.db.UserEntity;
import com.example.projectstudy.db.UserRepository;
import com.example.projectstudy.db.diet_inform.dto.Diet_Inform_Comment_Dto;
import com.example.projectstudy.db.diet_inform.dto.Diet_Inform_Post_Dto;
import com.example.projectstudy.db.diet_inform.entities.*;
import com.example.projectstudy.db.diet_inform.repos.Diet_Inform_Article_Img_Repository;
import com.example.projectstudy.db.diet_inform.repos.Diet_Inform_Article_Repository;
import com.example.projectstudy.db.diet_inform.repos.Diet_Inform_Comment_Repository;
import com.example.projectstudy.db.diet_inform.repos.Diet_Inform_Likes_Repository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// 자유게시판 헬스장게시판 헬스정보게시판(물품대여) 오운완챌리지  ->  api 헬스장  채팅기능  (등업제도 / 유저 업데이트)

// 자유게시판 헬스장게시판(네이머api) // 헬스정보게시판(태그 형식) 채팅기능구현  + 물품대여게시판 //

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/healthInform")
public class HealthInFormController {

    private final Diet_Inform_Article_Repository dietInformArticleRepository;
    private final Diet_Inform_Article_Img_Repository dietInformArticleImgRepository;
    private final Diet_Inform_Comment_Repository dietInformCommentRepository;
    private final UserRepository userRepository;
    private final Diet_Inform_Likes_Repository dietInformLikesRepository;

    // 헬스 정보 공유 게시판 메인

    @GetMapping("/main")
    public String healthInformMain(){

        return "healthMain";
    }

    // 다이어트 게시판 전체 목록
    @GetMapping("/diet")
    public String dietInform(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        // 페이지네이션 정보 생성
        Pageable pageable = PageRequest.of(page, 10); // 한 페이지에 10개의 게시글 표시

        // "공지사항" 태그 게시물 가져오기
        Page<Diet_Inform_Article> noticeArticles = dietInformArticleRepository.findByTag("공지사항", pageable);

        // 나머지 게시물 가져오기 (공지사항 제외)
        Page<Diet_Inform_Article> otherArticles = dietInformArticleRepository.findByTagNot("공지사항", pageable);

        List<Diet_Inform_Article> allArticles = new ArrayList<>();
        allArticles.addAll(noticeArticles.getContent());
        allArticles.addAll(otherArticles.getContent());

        // 전체 게시물 리스트를 페이지네이션으로 다시 생성
        Page<Diet_Inform_Article> articlePage = new PageImpl<>(allArticles, pageable, noticeArticles.getTotalElements() + otherArticles.getTotalElements());

        model.addAttribute("articles", articlePage);

        return "DietInform";
    }


    // 다이어트 게시판 작성자로 찾기
    @GetMapping("/diet/search/byAuthor")
    public String dietInformByAuthor(
            @RequestParam(name = "author") String author,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        // 페이지네이션 정보 생성
        Pageable pageable = PageRequest.of(page, 10); // 한 페이지에 10개의 게시글 표시

        // 작성자로 게시물 목록 페이지별로 가져오기
        Page<Diet_Inform_Article> articlePage = dietInformArticleRepository.findByUser_UsernameIgnoreCaseContaining(author, pageable);

        model.addAttribute("articles", articlePage);

        return "DietInform";
    }

    @GetMapping("/diet/search/byTag")
    public String dietInformByTag(
            @RequestParam(name = "tag") String tag,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        // 페이지네이션 정보 생성
        Pageable pageable = PageRequest.of(page, 10); // 한 페이지에 10개의 게시글 표시

        // 태그로 게시물 목록 페이지별로 가져오기
        Page<Diet_Inform_Article> articlePage = dietInformArticleRepository.findByTagIgnoreCaseContaining(tag, pageable);

        model.addAttribute("articles", articlePage);

        return "DietInform";
    }

    @GetMapping("/diet/search/byTitle")
    public String dietInformByTitle(
            @RequestParam(name = "title") String title,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        // 페이지네이션 정보 생성
        Pageable pageable = PageRequest.of(page, 10); // 한 페이지에 10개의 게시글 표시

        // 제목으로 게시물 목록 페이지별로 가져오기
        Page<Diet_Inform_Article> articlePage = dietInformArticleRepository.findByTitleIgnoreCaseContaining(title, pageable);

        model.addAttribute("articles", articlePage);

        return "DietInform";
    }

    // 다이어트 게시판 단일 조회
    @GetMapping("/diet/{id}")
    public String dietInformArticle(@PathVariable("id") Long id, Model model){

        Optional<Diet_Inform_Article> dietInformArticle = dietInformArticleRepository.findById(id);
        if (dietInformArticle.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        // 이미지
        List<Diet_Inform_Article_Img> dietInformArticleImgs = dietInformArticleImgRepository.findAll();
        List<Diet_Inform_Article_Img> dietInformArticleImgList = new ArrayList<>();

        for (Diet_Inform_Article_Img Img : dietInformArticleImgs ) {
            if(Img.getDietInformArticle().getId() == id){
                dietInformArticleImgList.add(Img);
            }
        }

        List<Diet_Inform_Comment> diet_inform_comments = dietInformCommentRepository.findAll();
        List<Diet_Inform_Comment> article_comments = new ArrayList<>();

        for (Diet_Inform_Comment comment: diet_inform_comments) {
            if(comment.getDietInformArticle().getId() == id){
                article_comments.add(comment);
            }
        }

        List<Diet_Inform_Likes> dietInformLikes = dietInformLikesRepository.findAll();
        int like = 0;

        for (Diet_Inform_Likes likes: dietInformLikes ) {
            if(likes.getDietInformArticle().getId() == id){
                like ++;
            }
        }

        model.addAttribute("like", like);
        model.addAttribute("article", dietInformArticle.get());
        model.addAttribute("articleImgs", dietInformArticleImgList);
        model.addAttribute("articleComments", article_comments);
        return "DietInformArticle";
    }


    // 좋아요 로직 (좋아요는 한번 더 누르면 취소)
    @Transactional
    @PostMapping("/diet/like/{articleId}")
    public ResponseEntity<String> postLike(@PathVariable("articleId") Long articleId){

        log.info("좋아요 로직 실행");

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 좋아요 하려고 하는 피드가 있는지 확인
        Optional<Diet_Inform_Article> diet_inform_article = dietInformArticleRepository.findById(articleId);

        if(diet_inform_article.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Diet_Inform_Article dietInformArticle = diet_inform_article.get();

        // 좋아요 하려고 하는 피드가 자신이 작성한 피드인지 확인
        if(dietInformArticle.getUser().getUsername().equals(username)){
            return ResponseEntity.badRequest().body("자신이 작성한 피드에는 좋아요는 못 누른다");
        }

        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        UserEntity user = userEntity.get();
        Long userId = user.getId();

        Optional<Diet_Inform_Likes> likesOptional = dietInformLikesRepository.findByUserIdAndDietInformArticleId(userId, articleId);
        // 해당 피드에 현재 사용자가 좋아요를 누른적이 없다면 좋아요

        if(likesOptional.isEmpty()){
            Diet_Inform_Likes dietInformLikes = new Diet_Inform_Likes();
            dietInformLikes.setDietInformArticle(dietInformArticle);
            dietInformLikes.setUser(user);
            dietInformLikesRepository.save(dietInformLikes);
            log.info("좋아요");
            return ResponseEntity.ok("좋아요!");
        }
        // 이미 눌렀으면 삭제
        else{
            dietInformLikesRepository.deleteByDietInformArticleIdAndUserId(articleId, userId);
            log.info("좋아요 삭제");
            return ResponseEntity.ok("좋아요 삭제!");
        }

    }

    // 댓글 로직
    @PostMapping("/diet/comment/{articleId}")
    public ResponseEntity<String> postComment(@PathVariable("articleId") Long articleId,
                                              @Valid @RequestBody Diet_Inform_Comment_Dto requestDto,
                                              LocalDateTime time) {

        Optional<Diet_Inform_Article> dietInformArticle = dietInformArticleRepository.findById(articleId);

        if (dietInformArticle.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Diet_Inform_Article diet_inform_article = dietInformArticle.get();

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<UserEntity> user = userRepository.findByUsername(username);
        if (user.isEmpty()){
            return ResponseEntity.badRequest().body("Bad request");
        }

        // 댓글 작성 로직 (requestDto의 comment 사용)
        Diet_Inform_Comment diet_inform_comment = new Diet_Inform_Comment();
        diet_inform_comment.setDietInformArticle(diet_inform_article); // 게시글
        diet_inform_comment.setUser(user.get()); // 작성자
        diet_inform_comment.setContent(requestDto.getComment()); // 댓글내용

        LocalDateTime now = time.now();
        diet_inform_comment.setCreatedAt(now); // 댓글 작성 시간

        dietInformCommentRepository.save(diet_inform_comment);

        return ResponseEntity.ok("Comment posted successfully");
    }

    // 댓글 삭제 (댓글은 수정 불가능)
    @DeleteMapping("/diet/comment/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable("commentId") Long commentId) {

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 댓글 삭제 로직 수행
        Optional<Diet_Inform_Comment> comment = dietInformCommentRepository.findById(commentId);
        if (comment.isEmpty()){
            ResponseEntity.notFound();
        }


        if((!comment.get().getUser().getUsername().equals(username))){
            // 관리자도 아니면 badRequest
            if(!"admin".equals(username)){
                ResponseEntity.badRequest();
            }
        }
        dietInformCommentRepository.delete(comment.get());

        return ResponseEntity.ok().build();
    }

    // 다이어트 게시판 삭제 요청
    @DeleteMapping("/diet/delete/{articleId}")
    public ResponseEntity<String> dietInformDelete(@PathVariable("articleId") Long articleId){
        Optional<Diet_Inform_Article> dietInformArticle = dietInformArticleRepository.findById(articleId);

        if (dietInformArticle.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Diet_Inform_Article diet_inform_article = dietInformArticle.get();

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 삭제

        //  댓글 삭제
        List<Diet_Inform_Comment> commentList = dietInformCommentRepository.findAll();
        for (Diet_Inform_Comment comment: commentList) {
            if (comment.getDietInformArticle().getId() == articleId){
               if(!comment.getUser().getUsername().equals(username)){
                   if(!"admin".equals(username)){
                       return ResponseEntity.badRequest().body("bad request");
                   }
               }
               dietInformCommentRepository.delete(comment);
            }
        }

        // 좋아요 삭제
        List<Diet_Inform_Likes> dietInformLikes = dietInformLikesRepository.findAll();

        for (Diet_Inform_Likes likes: dietInformLikes ) {
            if(likes.getDietInformArticle().getId() == articleId){
                if(!likes.getUser().getUsername().equals(username)){
                    if("admin".equals(username)){
                        return ResponseEntity.badRequest().body("bad request");
                    }
                }

                dietInformLikesRepository.delete(likes);
            }
        }
        // 이미지 삭제
        List<Diet_Inform_Article_Img> ImgList = dietInformArticleImgRepository.findAll();

        List<Diet_Inform_Article_Img> articleImg = new ArrayList<>();
        for (Diet_Inform_Article_Img Img: ImgList) {
            if(Img.getDietInformArticle().getId() == articleId){

                if(!Img.getDietInformArticle().getUser().getUsername().equals(username)){
                    if(!"admin".equals(username)){
                        return ResponseEntity.badRequest().body("bad request");
                    }
                }

                articleImg.add(Img);
            }
        }


        for (int i = 0; i < articleImg.size() ; i++) {
            String[] split = articleImg.get(i).getImgUrl().split("/");
            String name = split[split.length-1];
            String imagePath = "media/diet/" + articleId + "/" + name;

            // 실제 서버에서 이미지 삭제
            try {
                Files.delete(Path.of(imagePath));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            // DB에서 이미지 삭제
            dietInformArticleImgRepository.delete(articleImg.get(i));
        }

        // Article 삭제
        if (diet_inform_article.getUser().getUsername().equals(username)||"admin".equals(username)){
            dietInformArticleRepository.delete(diet_inform_article);
            return ResponseEntity.ok("Article deleted successfully");
        }

        return ResponseEntity.badRequest().body("bad request");
    }

    // 다이어트 게시판 글 수정 view
    @GetMapping("/diet/modify/{articleId}")
    public String dietInformModifyPage(@PathVariable("articleId") Long articleId, Model model){

        Optional<Diet_Inform_Article> dietInformArticle = dietInformArticleRepository.findById(articleId);
        if (dietInformArticle.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        // 이미지
        List<Diet_Inform_Article_Img> dietInformArticleImgs = dietInformArticleImgRepository.findAll();
        List<Diet_Inform_Article_Img> dietInformArticleImgList = new ArrayList<>();

        for (Diet_Inform_Article_Img Img : dietInformArticleImgs ) {
            if(Img.getDietInformArticle().getId() == articleId){
                dietInformArticleImgList.add(Img);
            }
        }
        model.addAttribute("article", dietInformArticle.get());
        model.addAttribute("articleImgs", dietInformArticleImgList);
        model.addAttribute("isEditMode", false);

        return "DietInformModify";
    }

    // 다이어트 게시판 글 수정
    @PutMapping("/diet/modify/{articleId}")
    public ResponseEntity<String> dietInformModify(@PathVariable("articleId") Long articleId,
     @RequestBody Diet_Inform_Post_Dto updatedArticleDto
    ){
        Optional<Diet_Inform_Article> dietInformArticle = dietInformArticleRepository.findById(articleId);

        if (dietInformArticle.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Diet_Inform_Article diet_inform_article = dietInformArticle.get();

        // 현재 사용자 정보 가져오기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 수정 로직
        diet_inform_article.setTag(updatedArticleDto.getTag());
        diet_inform_article.setTitle(updatedArticleDto.getTitle());
        diet_inform_article.setContent(updatedArticleDto.getContent());

        // 수정
        if (diet_inform_article.getUser().getUsername().equals(username)||"admin".equals(username)){
            dietInformArticleRepository.save(diet_inform_article);
            return ResponseEntity.ok("Article modify successfully");
        }
        return ResponseEntity.badRequest().body("bad request");
    }

    // 다이어트 게시판 수정하면서 단일 이미지 삭제
    @DeleteMapping("/diet/modify/delete/{ImagesId}")
    public ResponseEntity<String> ModifyDeleteImage(@PathVariable("ImagesId") Long ImagesId ){

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        log.info("이미지 삭제 로직 실행");
        Optional<Diet_Inform_Article_Img> optional = dietInformArticleImgRepository.findById(ImagesId);
        if (optional.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Diet_Inform_Article_Img dietInformArticleImg = optional.get();
        if (!dietInformArticleImg.getDietInformArticle().getUser().getUsername().equals(username)){
            if(!"admin".equals(username)){
                return ResponseEntity.badRequest().body("badRequest");
            }
        }

        dietInformArticleImgRepository.delete(dietInformArticleImg);

        // 서버에서 이미지 삭제
        Long articleId = dietInformArticleImg.getDietInformArticle().getId();
        String[] split = dietInformArticleImg.getImgUrl().split("/");
        String name = split[split.length-1];
        String imagePath = "media/diet/" + articleId + "/" + name;

        // 실제 서버에서 이미지 삭제
        try {
            Files.delete(Path.of(imagePath));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok("이미지 삭제");
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
                    ImagesEntity.setDietInformArticle(dietInformArticle);
                    ImagesEntity.setImgUrl(String.format("/static.js/diet/%d/%s", id, originalFilename));
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
