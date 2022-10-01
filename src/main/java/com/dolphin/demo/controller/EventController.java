package com.dolphin.demo.controller;

import com.dolphin.demo.domain.Event;
import com.dolphin.demo.dto.request.EventRequestDto;
import com.dolphin.demo.dto.response.EventResponseDto;
import com.dolphin.demo.repository.EventRepository;
import com.dolphin.demo.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@RestController
public class EventController {

    private final EventRepository eventRepository;

    private final EventService eventService;


    // 등록된 축제 리스트 조회하기
    @GetMapping("/api/auth/events")
    public ResponseEntity getEvents() {

        List<Event> events = eventRepository.findAll();

        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    // 이벤트 등록하기
    @PostMapping("/api/auth/event")
    public ResponseEntity<EventResponseDto> createEvent(@RequestPart(value = "data") EventRequestDto eventRequestDto,
                                                        @RequestPart(value = "imageUrl", required = false) MultipartFile multipartFile) throws IOException {

        return eventService.createEvent(eventRequestDto, multipartFile);
    }

    // 이벤트 수정하기
    @PutMapping("/api/auth/event/{id}")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable Long id,
                                                        @RequestPart(value = "data") EventRequestDto eventRequestDto,
                                                        @RequestPart(value = "imageUrl", required = false) MultipartFile multipartFile) throws IOException {

        return eventService.updateEvent(id, eventRequestDto, multipartFile);
    }

    // 이벤트 삭제하기
    @DeleteMapping("/api/auth/event/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id) {

        eventService.deleteEvent(id);
        return ResponseEntity.ok().body("Delete Event : "+ id);
    }
}
