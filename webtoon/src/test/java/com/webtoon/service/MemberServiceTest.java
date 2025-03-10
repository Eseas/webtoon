//package com.webtoon.service;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//
//import com.webtoon.domain.User.Member;
//import jakarta.persistence.EntityManager;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//@SpringBootTest
//@ActiveProfiles("test")
//public class MemberServiceTest {
//
//    @Autowired
//    private EntityManager entityManager;
//
//    @Test
//    @Transactional
//    public void testFindByLoginIdAndUsingState() {
//        // Test for a member that should exist
//        String loginId = "admin";
//        String usingState = "US001";
//        entityManager.clear();
//
//        // Native Query 실행
//        // Native Query 실행 (인덱스 없이 직접 값을 넣음)
//        String query = "SELECT * FROM webtoon.member WHERE login_id = '" + loginId + "' AND using_state = '" + usingState + "'";
//        List<Member> resultList = entityManager.createNativeQuery(query, Member.class)
//                .getResultList();
//
//        // 데이터가 있을 경우
//        Optional<Member> member = resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
//        assertTrue(member.isPresent(), "Expected to find a member with loginId 'admin' and usingState 'US001'");
//    }
//}
