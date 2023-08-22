package com.foodielog.server._core.security;

import com.foodielog.server._core.error.exception.Exception401;
import com.foodielog.server._core.error.exception.Exception403;
import com.foodielog.server._core.security.jwt.JwtAuthenticationFilter;
import com.foodielog.server._core.security.jwt.JwtExceptionFilter;
import com.foodielog.server._core.util.FilterResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 1. CSRF 해제
        http.csrf().disable(); // postman 접근 해야 함!! - CSR 할때!!

        // 2. iframe 거부
        http.headers().frameOptions().sameOrigin();

        // 3. cors 재설정
        http.cors().configurationSource(configurationSource());

        // 4. jSessionId 사용 거부
        // STATELESS -> 응답이 끝나기 전 까지는 세션에 저장 된다. 응답이 끝나면 삭제
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 5. form 로그인 해제 (UsernamePasswordAuthenticationFilter 비활성화)
        http.formLogin().disable();

        // 6. username, password 헤더 로그인 방식 해제 (BasicAuthenticationFilter 비활성화)
        http.httpBasic().disable();

        // 7. JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞에 넣는다
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtExceptionFilter, jwtAuthenticationFilter.getClass());

        // 8. 인증 실패 처리
        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
            log.warn("인증되지 않은 사용자가 자원에 접근하려 합니다 : " + authException.getMessage());
            FilterResponseUtils.unAuthorized(response, new Exception401("인증되지 않았습니다"));
        });

        // 9. 권한 실패 처리
        http.exceptionHandling().accessDeniedHandler((request, response, accessDeniedException) -> {
            log.warn("권한이 없는 사용자가 자원에 접근하려 합니다 : " + accessDeniedException.getMessage());
            FilterResponseUtils.forbidden(response, new Exception403("권한이 없습니다"));
        });

        // 11. 인증, 권한 필터 설정
        http.authorizeRequests(
                // '/api' 로 시작 하는 url 은 로그인 필요제
                // @Todo "/h2-console/**" 접근은 개발 시에만 열어 두고 배포시 제거
                authorize -> authorize.antMatchers("/auth/**", "/h2-console/**").permitAll()    // 누구나 접근 가능
                        .antMatchers("/api/**").hasAnyAuthority("USER", "ADMIN")
                        .antMatchers("/admin/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
        );

        return http.build();
    }

    // cors 설정
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedHeader("*");
        config.addAllowedMethod("*"); // GET, POST, PUT, DELETE (+Javascript 요청 허용)
        config.addAllowedOriginPattern("*"); // 모든 IP 주소 허용 (프론트앤드 IP만 허용 하도록 변경 필요)
        config.setAllowCredentials(true); // 클라이언트에서 쿠키 요청 허용
        config.addExposedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 모든 url에 대해서 설정을 적용한다
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
            Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}