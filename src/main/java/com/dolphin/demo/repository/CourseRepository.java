package com.dolphin.demo.repository;


import com.dolphin.demo.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CourseRepository extends JpaRepository<Course, Long> {
    // 마이페이지의 내가 쓴 리뷰를 불러올 때
    List<Course> findAllByMemberId(Long member_id);

}
