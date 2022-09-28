package com.dolphin.demo.repository;

import com.dolphin.demo.domain.Heart;
import com.dolphin.demo.domain.Member;
import com.dolphin.demo.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface HeartRepository extends JpaRepository<Heart, Long> {

    int countByPlace(Place place);
    Optional<Heart> findByMember(Member member);
    void deleteAllByPlace(Place place);
}
