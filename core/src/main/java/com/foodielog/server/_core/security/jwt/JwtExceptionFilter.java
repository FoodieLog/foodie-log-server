package com.foodielog.server._core.security.jwt;

import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.util.FilterResponseUtils;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * 인증 오류가 아닌, JWT 관련 오류만 핸들링 하는 필터.
 * */
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException ex) {
            setErrorResponse(request, response, ex);
        }
    }

    public void setErrorResponse(HttpServletRequest req, HttpServletResponse res, Throwable ex) throws IOException {
        FilterResponseUtils.badRequest(res, new Exception400("JWT", ex.getMessage()));
    }
}
