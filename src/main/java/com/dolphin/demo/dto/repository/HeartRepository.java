package com.dolphin.demo.dto.repository;

import com.dolphin.demo.domain.Heart;
import com.dolphin.demo.domain.Member;
import com.dolphin.demo.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface HeartRepository extends JpaRepository<Heart, Long> {

    int countByPlace(Place place);
    Optional<Heart> findByMemberAndPlace(Member member, Place place);
    void deleteAllByPlace(Place place);
    List<Heart> findAllByMember(Member member);
}
