package com.example.springedu2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// SpringSecurity 의 설정
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 서버가 내려주는 csrf 토큰 값을 사용하지 않는다.
                // visitorForm.html 순수 html 이라 서버의 csrf 토큰을 보관 및 처리가 불가능 하다.
                .csrf(csrf -> csrf.disable()) // 실무는 설정, 공부할때는 주석(설정안함)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/index.html",
                                "/css/**", "/img/**", "/js/**", "/fonts/**",
                                "/login", "/members/register"
                        ).permitAll() // 로그인없이 사용가능하다
                        .requestMatchers("/admin/**", "/vupdate", "/vdelete").hasRole("ADMIN")
                        .requestMatchers(
                                "/visitorMain.html", "/visitorForm.html",
                                "/vlist", "/vinsert", "/vsearch", "/one",
                                "/members/me"
                        ).authenticated() // 로그인이 필요한 곳
                        .anyRequest().authenticated() // 설정하지 않은 다른 요청도 로그인 필요
                )
                // formLogin() 은 사용자가 <form>으로 입력한 username, password 를 기반으로 인증 처리
                .formLogin(form -> form
                        .loginPage("/login")
                        // GET /login -> PageController 에 /login 주소로 이동 -> login.html 로 보낸다
                        // 내가 만든 로그인 화면으로 사용하겠음 이라는 뜻
                        .loginProcessingUrl("/login") // 생략가능
                        // Post /login
                        // SpringSecurity 가 username, password 를 읽어서 인증처리한다. : 자동
                        .defaultSuccessUrl("/visitorMain.html", true)
                        .permitAll()
                )
                .logout(logout -> logout.logoutUrl("/logout"))
                .exceptionHandling(exception -> exception.accessDeniedPage("/access-denied")
                ); // 접근 거부 페이지 처리
        return http.build();
    }

    // 비밀번호를 암호화
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
