package com.dolphin.demo.repository;

import com.dolphin.demo.domain.Member;
import com.dolphin.demo.domain.OutMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OutMemberRepository extends JpaRepository<OutMember, Long> {

    Boolean existsByUsername(String membername);
}
