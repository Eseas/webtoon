package com.webtoon.domain.User;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"Id\"")  // 테이블에서 "Id"로 정의된 컬럼명을 명시적으로 설정
    private Long id;

    @Column(name = "login_id", unique = true, nullable = false, length = 50)
    private String loginId;

    @Column(name = "pwd", nullable = false, length = 50)
    private String pwd;

    @Column(name = "first_name", nullable = false, length = 10)
    private String first_name;

    @Column(name = "last_name", nullable = false, length = 20)
    private String last_name;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "reg_no", nullable = false, length = 30)
    private String reg_no;

    @Column(name = "age", nullable = false)
    private Short age;

    @Column(name = "phone_num", nullable = false, length = 20)
    private String phone_num;

    @Column(name = "wrong_pwd_cnt", nullable = false)
    private Short wrong_pwd_cnt = 0;  // 기본값 설정

    @Column(name = "using_state", nullable = false, length = 5)
    private String using_state;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_dt", nullable = false)
    private Date created_dt;

    @Column(name = "created_id", nullable = false)
    private String created_id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_dt", nullable = false)
    private Date updated_dt;

    @Column(name = "updated_id", nullable = false)
    private String updated_id;

    @Column(name = "social_token", length = 50)
    private String social_token;

    @Builder
    public Member(Long id, String loginId, String pwd, String first_name, String last_name, String email, String reg_no, Short age, String phone_num, Short wrong_pwd_cnt, String using_state, Date created_dt, String created_id, Date updated_dt, String updated_id, String social_token) {
        this.id = id;
        this.loginId = loginId;
        this.pwd = pwd;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.reg_no = reg_no;
        this.age = age;
        this.phone_num = phone_num;
        this.wrong_pwd_cnt = wrong_pwd_cnt;
        this.using_state = using_state;
        this.created_dt = created_dt;
        this.created_id = created_id;
        this.updated_dt = updated_dt;
        this.updated_id = updated_id;
        this.social_token = social_token;
    }
}
