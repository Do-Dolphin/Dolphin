package com.dolphin.demo.service;

import com.dolphin.demo.domain.*;
import com.dolphin.demo.dto.request.CourseDataRequestDto;
import com.dolphin.demo.dto.request.CourseRequestDto;
import com.dolphin.demo.dto.response.CourseListResponseDto;
import com.dolphin.demo.dto.response.CoursePlaceResponseDto;
import com.dolphin.demo.dto.response.CourseResponseDto;
import com.dolphin.demo.exception.CustomException;
import com.dolphin.demo.exception.ErrorCode;
import com.dolphin.demo.jwt.UserDetailsImpl;
import com.dolphin.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
public class CourseService {

    private final PlaceRepository placeRepository;
    private final PlaceImageRepository imageRepository;
    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;
    private final CourseItemRepository itemRepository;


    public ResponseEntity<List<CourseListResponseDto>> getCourseList(UserDetailsImpl userDetails) {
        if (userDetails == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        List<Course> courseList = courseRepository.findAllByMemberId(member.getId());
        List<CourseListResponseDto> responseDtoList = new ArrayList<>();
        for (Course course:courseList) {
            responseDtoList.add(CourseListResponseDto.builder()
                    .id(course.getId())
                    .name(course.getName())
                    .build());
        }
        return ResponseEntity.ok().body(responseDtoList);
    }

    //코스 조회하는 메서드
    public ResponseEntity<CourseResponseDto> getCourse(UserDetailsImpl userDetails, Long id) {
        if (userDetails == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        Course course = courseRepository.findById(id).orElse(null);
        if(course == null)
            throw new CustomException(ErrorCode.NOT_FOUND_COURSE);

        List<CourseItem> courseItems = itemRepository.findAllByCourseId(course.getId());
        List<CoursePlaceResponseDto> responseDtoList = new ArrayList<>();

        for (CourseItem item:courseItems) {
            Place place = item.getPlace();
            PlaceImage img = imageRepository.findFirstByPlace(place).orElse(null);
            String url = null;
            if(img != null)
                url = img.getImageUrl();
            responseDtoList.add(CoursePlaceResponseDto.builder()
                    .id(place.getId())
                    .title(place.getTitle())
                    .star(place.getStar())
                    .theme(place.getTheme())
                    .image(url)
                    .adress(place.getAddress())
                    .mapX(place.getMapX())
                    .mapY(place.getMapY())
                    .build());
        }

        return ResponseEntity.ok().body(CourseResponseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .data(responseDtoList)
                .build());
    }

    public ResponseEntity<CourseResponseDto> creatCourse(UserDetailsImpl userDetails, CourseRequestDto requestDto) {
        if (userDetails == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        Course course = Course.builder()
                .member(member)
                .name(requestDto.getName())
                .build();

        List<CourseItem> courseItems = new ArrayList<>();
        for (CourseDataRequestDto dto: requestDto.getData()) {
            Place place = placeRepository.findById(dto.getPlaceId()).orElse(null);
            if(place == null)
                throw new CustomException(ErrorCode.NOT_FOUND_PLACE);
            courseItems.add(CourseItem.builder()
                    .course(course)
                    .place(place)
                    .build());
        }

        courseRepository.save(course);
        itemRepository.saveAll(courseItems);

        List<CoursePlaceResponseDto> responseDtoList = new ArrayList<>();

        for (CourseItem item:courseItems) {
            Place place = item.getPlace();
            PlaceImage img = imageRepository.findFirstByPlace(place).orElse(null);
            String url = null;
            if(img != null)
                url = img.getImageUrl();
            responseDtoList.add(CoursePlaceResponseDto.builder()
                    .id(place.getId())
                    .title(place.getTitle())
                    .star(place.getStar())
                    .theme(place.getTheme())
                    .image(url)
                    .adress(place.getAddress())
                    .mapX(place.getMapX())
                    .mapY(place.getMapY())
                    .build());
        }


        return ResponseEntity.ok().body(CourseResponseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .data(responseDtoList)
                .build());
    }



    public ResponseEntity<CourseResponseDto> addCoursePlace(UserDetailsImpl userDetails, List<CourseDataRequestDto> requestDtos, Long id) {
        if (userDetails == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        Course course = courseRepository.findById(id).orElse(null);
        if(course == null)
            throw new CustomException(ErrorCode.NOT_FOUND_COURSE);

        List<CourseItem> courseItems = itemRepository.findAllByCourseId(id);
        for (CourseDataRequestDto dto: requestDtos) {
            Place place = placeRepository.findById(dto.getPlaceId()).orElse(null);
            if(place == null)
                throw new CustomException(ErrorCode.NOT_FOUND_PLACE);
            courseItems.add(CourseItem.builder()
                    .course(course)
                    .place(place)
                    .build());
        }

        itemRepository.saveAll(courseItems);

        List<CoursePlaceResponseDto> responseDtoList = new ArrayList<>();

        for (CourseItem item:courseItems) {
            Place place = item.getPlace();
            PlaceImage img = imageRepository.findFirstByPlace(place).orElse(null);
            String url = null;
            if(img != null)
                url = img.getImageUrl();
            responseDtoList.add(CoursePlaceResponseDto.builder()
                    .id(place.getId())
                    .title(place.getTitle())
                    .star(place.getStar())
                    .theme(place.getTheme())
                    .image(url)
                    .adress(place.getAddress())
                    .mapX(place.getMapX())
                    .mapY(place.getMapY())
                    .build());
        }


        return ResponseEntity.ok().body(CourseResponseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .data(responseDtoList)
                .build());
    }

    public ResponseEntity<String> deleteCourse(UserDetailsImpl userDetails, Long id) {
        if (userDetails == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        Course course = courseRepository.findById(id).orElse(null);
        if(course == null)
            throw new CustomException(ErrorCode.NOT_FOUND_COURSE);

        if(!course.getMember().getUsername().equals(member.getUsername()))
            throw new CustomException(ErrorCode.DO_NOT_MATCH_USER);

        courseRepository.delete(course);

        return ResponseEntity.ok().body("delete course: "+id);
    }

}
