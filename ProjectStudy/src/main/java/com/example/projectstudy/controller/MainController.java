package com.example.projectstudy.controller;

import com.example.projectstudy.security.details.CustomUserDetails;
import com.example.projectstudy.security.details.JpaUserDetailsManager;
import com.example.projectstudy.security.dto.LoginForm;
import com.example.projectstudy.security.dto.UserForm;
import com.example.projectstudy.security.jwt.JwtTokenUtils;
import com.example.projectstudy.service.MainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@Controller
@RequestMapping("/main")
public class MainController {

    // 의존성 주입
    public MainController(MainService mainService, JpaUserDetailsManager userDetailsManager, UserDetailsManager manager,
         PasswordEncoder passwordEncoder,  JwtTokenUtils jwtTokenUtils
    ) {
        this.mainService = mainService;
        this.userDetailsManager = userDetailsManager;
        this.manager = manager;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtils = jwtTokenUtils;
    }
    private final MainService mainService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsManager manager;
    private final JpaUserDetailsManager userDetailsManager;

    private final JwtTokenUtils jwtTokenUtils;

    @GetMapping
    public String mainPage(){
        return "main";
    }

    // 회원가입 view
    @GetMapping("/sign-up")
    public String registerPage(){
        return "sign-up";
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> registerPost(@RequestBody UserForm userForm){

        if (!userForm.getPassword().equals(userForm.getPasswordCheck())){
            log.info("password not match"); // password  passwordCheck not match
            return ResponseEntity.badRequest().body("Password and passwordCheck do not match");
        }

        if (userDetailsManager.userExists(userForm.getUsername())){
            log.info("user already exists"); // 유저 이미 존재
            return ResponseEntity.badRequest().body("User already exists");
        }

        manager.createUser(CustomUserDetails.builder()
                .username(userForm.getUsername())
                .password(passwordEncoder.encode(userForm.getPassword()))
                .email(userForm.getEmail())
                .build()
        );

        // 회원 가입이 성공하면 로그인 페이지로 리다이렉트
        return ResponseEntity.ok("User registration successful");
    }

    // 로그인 view
    @GetMapping("/login")
    public String loginPage(){

        return "login";
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginPost(@RequestBody LoginForm loginForm){

        UserDetails userDetails = manager.loadUserByUsername(loginForm.getUsername());

        if(!passwordEncoder.matches(loginForm.getPassword(), userDetails.getPassword())) {
            log.info("Password incorrect"); // 잘못된 비밀번호
            return ResponseEntity.badRequest().body("Password incorrect");
        }

        String jwt = jwtTokenUtils.generateToken(userDetails);

        // 로그인 성공하면 토큰 실어서 응답하기
        return ResponseEntity.ok(jwt);

    }

    // localhost:8080/main/auth 로그인 on 메인 페이지

    @GetMapping("/auth")
    public String mainAuthPage() {
        return "mainAuth";
    }


    // 자유 게시판
    @GetMapping("/freeBoard")
    public String freeBoardPage() {

        return "FreeBoard";
    }



}
