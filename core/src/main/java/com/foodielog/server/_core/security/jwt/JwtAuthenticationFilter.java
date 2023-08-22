package com.foodielog.server._core.security.jwt;

import com.foodielog.server._core.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * 1. JwtAuthenticationFilter 인증 처리 필터
 * 		- JWT 토큰을 검증 하고 사용자를 인증함
 * 		- JWT 토큰 파싱 -> 서명 유효성 검사 -> 만료 시간 검사 -> 사용자 인증
 *
 * 2. JwtAuthorizationFilter 인가 처리 필터
 * 		- 인증된 사용자의 요청이 접근 권한을 가지고 있는지 확인
 * 		- JWT 토큰 파싱 -> 사용자 식별 -> 권한 검사 -> 접근 제어
 *
 * Spring Security 설정 에서 접근 제어 설정( authorizeRequests() )을 했기 때문에
 *  JwtAuthenticationFilter 만 커스텀 한다.
 * */

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getHeader(HttpHeaders.AUTHORIZATION) == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtTokenProvider.resolveToken(request);

        if (jwtTokenProvider.isTokenValid(token) && !redisService.hasKey(token)) {
            // 토큰으로부터 유저 정보 받아오기
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            // SecurityContext 에 Authentication 객체 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
