package com.webtoon.repository.jpa;

import com.webtoon.domain.User.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findById(Long id);
    Optional<Member> findByLoginId(String login_id);

    @Query("SELECT m FROM Member m WHERE m.loginId = :loginId AND m.using_state = :usingState")
    Optional<Member> findByLoginIdAndUsingState(@Param("loginId") String loginId,
                                                @Param("usingState") String usingState
                                                );

    @Query("SELECT m FROM Member m WHERE m.loginId = :loginId AND m.using_state = :usingState AND m.social_code = :socialCode")
    Optional<Member> findBySocialLoginIdAndUsingState(@Param("loginId") String loginId,
                                                      @Param("usingState") String usingState,
                                                      @Param("socialCode") String socialCode
                                                      );
}
