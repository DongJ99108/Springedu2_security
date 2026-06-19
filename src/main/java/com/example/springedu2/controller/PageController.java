package com.example.springedu2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    // 로그인 페이지로 이동
    @GetMapping("/login")
    public String login() {
        return "login"; // login.html 호출
    }

    // 로그인 처리할 주소가 필요 -> 만들지 않는다. - 필요하지 않다는 뜻
    // @PostMapping("/login") 은 security filter 가 처리하므로 코딩 x
    // DB 처리 로직을 별도의 클래스에 구현해서 security 가 자동으로 호출처리
    // UserDetailsService 에서 loadUserByUsername() 실행 및 조회해서 결과를 반환
    // UserDetails 객체의 User 로 저장해서 SpringSecurity 에게 보낸다 : 로그인 ok

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied"; // access-denied.html
    }
}
