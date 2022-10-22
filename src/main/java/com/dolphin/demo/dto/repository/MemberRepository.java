package com.dolphin.demo.dto.repository;

import com.dolphin.demo.domain.Member;
import com.dolphin.demo.domain.MemberRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    Optional<Member> findByNickname(String nickname);

    Boolean existsByUsername(String membername);

    Optional<Member> findByUsernameAndRole(String username, MemberRoleEnum role);

}
