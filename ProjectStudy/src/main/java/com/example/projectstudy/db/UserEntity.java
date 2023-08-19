package com.example.projectstudy.db;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 필수항목
    @Column(nullable = false, unique = true)
    private String username; // 아이디
    @Column(nullable = false)
    private String password; // 비밀번호

    // 부수적으로 전화번호, 이메일, 주소 정보를 기입할 수 있다.
    private String address; // 주소
    private String email; // 이메일
    private String phone; // 전화번호

    private String profile_image;
}
