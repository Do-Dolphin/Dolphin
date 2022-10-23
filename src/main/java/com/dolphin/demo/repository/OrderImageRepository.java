package com.dolphin.demo.repository;

import com.dolphin.demo.domain.Order;
import com.dolphin.demo.domain.OrderImage;
import com.dolphin.demo.domain.Place;
import com.dolphin.demo.domain.PlaceImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface OrderImageRepository extends JpaRepository<OrderImage, Long> {

    List<OrderImage> findAllByOrder(Order order);


}
