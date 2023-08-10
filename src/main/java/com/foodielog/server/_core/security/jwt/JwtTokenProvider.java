package com.foodielog.server._core.security.jwt;

import java.security.Key;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.security.auth.PrincipalDetails;
import com.foodielog.server._core.security.auth.PrincipalDetailsService;
import com.foodielog.server.user.entity.User;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtTokenProvider {
	public static final Long EXP_ACCESS = 1000L * 60 * 60 * 24; // 24시간
	public static final Long EXP_REFRESH = 1000L * 60 * 60 * 24 * 30; // 30일
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER = "Authorization";

	private final PrincipalDetailsService principalDetailsService;

	private Key JWT_KEY;

	@Autowired
	public JwtTokenProvider(@Value("${jwt.key}") String secretKey, PrincipalDetailsService principalDetailsService) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		JWT_KEY = Keys.hmacShaKeyFor(keyBytes);

		this.principalDetailsService = principalDetailsService;
	}

	public String createAccessToken(User user) {
		String jwt = Jwts.builder()
			.setSubject(user.getEmail())
			.setExpiration(new Date(System.currentTimeMillis() + EXP_ACCESS))
			.claim("id", user.getId())
			.claim("role", String.valueOf(user.getRole()))
			.signWith(JWT_KEY, SignatureAlgorithm.HS256)
			.compact();
		return TOKEN_PREFIX + jwt;
	}

	public String createRefreshToken(User user) {
		String jwt = Jwts.builder()
			.setSubject(user.getEmail())
			.setExpiration(new Date(System.currentTimeMillis() + EXP_REFRESH))
			.claim("id", user.getId())
			.claim("role", String.valueOf(user.getRole()))
			.signWith(JWT_KEY, SignatureAlgorithm.HS256)
			.compact();
		return TOKEN_PREFIX + jwt;
	}

	// Request Header에서 token 값 추출
	public String resolveToken(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
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
			throw new Exception400("message", "Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			throw new Exception400("message", "Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			throw new Exception400("message", "Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			throw new Exception400("message", "Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			throw new Exception400("message", "JWT claims string is empty.");
		}
	}

	// 토큰으로 Authentication 객체 생성
	public Authentication getAuthentication(String jwt) {
		PrincipalDetails principalDetails = (PrincipalDetails)principalDetailsService.loadUserByUsername(getEmail(jwt));
		return new UsernamePasswordAuthenticationToken(principalDetails, "", principalDetails.getAuthorities());
	}

	// 토큰에서 email 정보 추출
	private String getEmail(String jwt) {
		return Jwts.parserBuilder()
			.setSigningKey(JWT_KEY)
			.build()
			.parseClaimsJws(jwt)
			.getBody()
			.getSubject();
	}

}
