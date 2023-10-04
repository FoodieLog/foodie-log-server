package com.foodielog.server._core.security.jwt;

import com.foodielog.server._core.redis.RedisService;
import com.foodielog.server._core.security.auth.PrincipalDetails;
import com.foodielog.server._core.security.auth.PrincipalDetailsService;
import com.foodielog.server.user.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    public static final Long EXP_ACCESS = 1000L * 60 * 30; // 30분
    public static final Long EXP_REFRESH = 1000L * 60 * 60 * 24 * 14; // 14일
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";

    private final PrincipalDetailsService principalDetailsService;
    private final RedisService redisService;

    private Key JWT_KEY;

    @Autowired
    public JwtTokenProvider(@Value("${jwt.key}") String secretKey, PrincipalDetailsService principalDetailsService, RedisService redisService) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        JWT_KEY = Keys.hmacShaKeyFor(keyBytes);

        this.principalDetailsService = principalDetailsService;
        this.redisService = redisService;
    }

    public String createAccessToken(User user) {
        String jwt = Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + EXP_ACCESS))
                .claim("id", user.getId())
                .claim("role", String.valueOf(user.getRole()))
                .signWith(JWT_KEY, SignatureAlgorithm.HS256)
                .compact();
        return jwt;
    }

    public String createRefreshToken(User user) {
        String jwt = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + EXP_REFRESH))
                .signWith(JWT_KEY, SignatureAlgorithm.HS256)
                .compact();
        return jwt;
    }

    // Request Header에서 token 값 추출
    public String resolveToken(String header) {
        return header.replaceAll(TOKEN_PREFIX, "");
    }

    // 토큰 검증
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(JWT_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            throw new JwtException("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            throw new JwtException("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            throw new JwtException("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new JwtException("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new JwtException("JWT claims string is empty.");
        }
    }

    public String invalidatedToken(String accessToken) {
        Long expiration = getExpiration(accessToken);
        String email = getEmail(accessToken);

        redisService.addBlacklist(accessToken, email, expiration);

        return email;
    }

    // 토큰으로 Authentication 객체 생성
    public Authentication getAuthentication(String jwt) {
        PrincipalDetails principalDetails = (PrincipalDetails) principalDetailsService.loadUserByUsername(getEmail(jwt));
        return new UsernamePasswordAuthenticationToken(principalDetails, "", principalDetails.getAuthorities());
    }

    // 토큰에서 email 정보 추출
    public String getEmail(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(JWT_KEY)
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }

    // 토큰에서 유효기간 추출
    public Long getExpiration(String jwt) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(JWT_KEY)
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getExpiration();

        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }
}
