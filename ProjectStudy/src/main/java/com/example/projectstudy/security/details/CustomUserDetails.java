package com.example.projectstudy.security.details;

import com.example.projectstudy.db.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    @Getter
    private Long id;

    private String username; // 아이디

    private String password; // 비밀번호
    private String address; // 주소
    private String email; // 이메일
    private String phone; // 전화번호
    private String profile_image; // 프로필 이미지

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public String getEmail() { return this.email;}
    public String getAddress() { return this.address;}
    public String getPhone() { return this.phone;}
    public String getProfile_image() { return  this.profile_image;}
    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }

    public static CustomUserDetails fromEntity(UserEntity entity) {
        return CustomUserDetails.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .address(entity.getAddress())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .profile_image(entity.getProfile_image())
                .build();
    }

    public UserEntity newEntity() {
        UserEntity entity = new UserEntity();
        entity.setUsername(username);
        entity.setPassword(password);
        entity.setAddress(address);
        entity.setEmail(email);
        entity.setPhone(phone);
        entity.setProfile_image(profile_image);
        return entity;
    }

}
