package com.dolphin.demo.service;

import com.dolphin.demo.domain.Event;
import com.dolphin.demo.dto.request.EventRequestDto;
import com.dolphin.demo.dto.response.EventResponseDto;
import com.dolphin.demo.exception.CustomException;
import com.dolphin.demo.exception.ErrorCode;
import com.dolphin.demo.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final AmazonS3Service amazonS3Service;


    // 행사 추가하기
    @Transactional
    public ResponseEntity<EventResponseDto> createEvent(EventRequestDto eventRequestDto, MultipartFile multipartFile) throws IOException {

        // 관리자가 맞는 여부 검증 추가 예정

        String imageUrl = amazonS3Service.upload(multipartFile);

        Event event = Event.builder()
                .title(eventRequestDto.getTitle())
                .linkUrl(eventRequestDto.getLinkUrl())
                .period(eventRequestDto.getPeriod())
                .imageUrl(imageUrl)
                .build();

        eventRepository.save(event);
        return ResponseEntity.ok().body(EventResponseDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .linkUrl(event.getLinkUrl())
                .period(event.getPeriod())
                .imageUrl(event.getImageUrl())
                .build());

    }

    // 행사 수정하기
    public ResponseEntity<EventResponseDto> updateEvent(Long event_id, EventRequestDto eventRequestDto, MultipartFile multipartFile) throws IOException {

        Event event = eventRepository.findById(event_id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_EVENT));

        // 관리자가 맞는지 여부 검증 추가 예정

        String imageUrl;
        if(multipartFile == null) {
            return ResponseEntity.ok().body(EventResponseDto.builder()
                    .id(event.getId())
                    .title(eventRequestDto.getTitle())
                    .linkUrl(eventRequestDto.getLinkUrl())
                    .period(eventRequestDto.getPeriod())
                    .imageUrl(event.getImageUrl())
                    .build());
        } else {
            // S3 저장소에서 삭제
            amazonS3Service.deleteFile(event.getImageUrl().substring(event.getImageUrl().lastIndexOf("/") + 1));
            imageUrl = amazonS3Service.upload(multipartFile);
        }

        // 수정된 내용 저장
        EventRequestDto updateEvent = EventRequestDto.builder()
                .title(eventRequestDto.getTitle())
                .linkUrl(eventRequestDto.getLinkUrl())
                .period(eventRequestDto.getPeriod())
                .imageUrl(imageUrl)
                .build();
        event.update(updateEvent);
        eventRepository.save(event);

        return ResponseEntity.ok().body(EventResponseDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .linkUrl(event.getLinkUrl())
                .period(event.getPeriod())
                .imageUrl(event.getImageUrl())
                .build());

    }

    // 행사 삭제하기
    public ResponseEntity<Long> deleteEvent(Long id) {
        // 관리자 여부 검증 추가 예정

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_EVENT));

        if (event.getImageUrl() != null) {
            amazonS3Service.deleteFile(event.getImageUrl().substring(event.getImageUrl().lastIndexOf("/") + 1));
        }
        eventRepository.delete(event);

        return ResponseEntity.ok().body(id);
    }
}
