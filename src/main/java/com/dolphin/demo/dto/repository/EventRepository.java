package com.dolphin.demo.dto.repository;

import com.dolphin.demo.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
}
