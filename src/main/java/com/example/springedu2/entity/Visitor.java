package com.example.springedu2.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Visitor {

    @Id // 기본키 : primary key
    @GeneratedValue(strategy = GenerationType.AUTO) // 번호 자동 증가
    private Integer id; // 방명록 번호 id : 기본키, @Id가 있는 칸은 무조건 wrapper 클래스로(int가 아닌 Integer)

    // 작성자 : name
    // NotBlank : null, ""(빈문자열), "  " : 공백 포함된 문자열 전부 허용 X
    // Size(max=50) : 문자열(50문자), 배열(50개), list(50개)
    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 50)
    private String name;

    // 작성일 : writeDate
    // data 등록일(Created Date) 자동입력 하나하나 LacalDateTime.now() 를 넣지 않아도 된다.
    @CreationTimestamp
    @Column(name = "writedate", nullable = false, updatable = false)
    private LocalDate writeDate;

    // 방명록 내용 : memo
    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 1000)
    private String memo;

}
