package com.example.projectstudy.security.jwt;

import com.example.projectstudy.security.details.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    public JwtTokenFilter(JwtTokenUtils jwtTokenUtils) {
        this.jwtTokenUtils = jwtTokenUtils;
    }

    // CRSF 공격이나 XSS 공격
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 쿠키에서 토큰 가져오기
        Cookie[] cookies = request.getCookies();
        String Token = null;
        if (cookies != null){
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    Token = cookie.getValue();
                    break;
                }
            }
        }
        if (Token != null){
           String token = Token;
           if (jwtTokenUtils.validate(token)){
               SecurityContext context = SecurityContextHolder.createEmptyContext();
               String username = jwtTokenUtils
                       .parseClaims(token)
                       .getSubject();
               // 사용자 인증 정보 생성
               AbstractAuthenticationToken authenticationToken
                       = new UsernamePasswordAuthenticationToken(
                       CustomUserDetails.builder()
                               .username(username)
                               .build(),
                       token, new ArrayList<>()
               );
               context.setAuthentication(authenticationToken);
               SecurityContextHolder.setContext(context);
               log.info("set security context with jwt");
           }
           else { log.warn("jwt validation failed");}
        }
        filterChain.doFilter(request, response);
    }
}
