package com.dolphin.demo.repository;

import com.dolphin.demo.domain.Member;
import com.dolphin.demo.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByOrderByStateAsc();
    List<Order> findAllByStateTrue();
    List<Order> findAllByMemberOrderByCreatedAtDesc(Member member);
}
