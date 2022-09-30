package com.dolphin.demo.repository;

import com.dolphin.demo.domain.Comment;
import com.dolphin.demo.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByOrderByStateAsc();
}
