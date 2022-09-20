package com.dolphin.demo.repository;

import com.dolphin.demo.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ImageRepository extends JpaRepository<Image, Long> {

}
