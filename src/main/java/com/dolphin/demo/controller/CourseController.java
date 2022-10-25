package com.dolphin.demo.controller;

import com.dolphin.demo.domain.Course;
import com.dolphin.demo.domain.CourseItem;
import com.dolphin.demo.domain.Place;
import com.dolphin.demo.dto.request.*;
import com.dolphin.demo.dto.response.CourseListResponseDto;
import com.dolphin.demo.dto.response.CourseResponseDto;
import com.dolphin.demo.dto.response.OrderListResponseDto;
import com.dolphin.demo.dto.response.OrderResponseDto;
import com.dolphin.demo.jwt.UserDetailsImpl;
import com.dolphin.demo.service.CourseService;
import com.dolphin.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;


@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
public class CourseController {

    private final CourseService courseService;


    @PostMapping("/course")
    public ResponseEntity<CourseResponseDto> createCourse(@RequestBody CourseRequestDto courseRequestDto,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return courseService.creatCourse(userDetails, courseRequestDto);
    }

    @GetMapping("/course")
    public ResponseEntity<List<CourseListResponseDto>> getCourseList(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        return courseService.getCourseList(userDetails);
    }

    @PostMapping("/course/{id}")
    public ResponseEntity<CourseResponseDto> addCoursePlace(@RequestBody List<CourseDataRequestDto> RequestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            @PathVariable Long id) {


        return courseService.addCoursePlace(userDetails, RequestDto, id);
    }

    //코스 조회
    @GetMapping("/course/{id}")
    public ResponseEntity<CourseResponseDto> getCourse(@PathVariable Long id,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return courseService.getCourse(userDetails, id);
    }

    @DeleteMapping("/course/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return courseService.deleteCourse(userDetails, id);
    }

    @PutMapping("/course/{id}")
    public ResponseEntity<CourseResponseDto> updateCourse(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @RequestBody CourseUpdateRequestDto courseRequestDto) {

        return courseService.updateCourse(userDetails,courseRequestDto, id);
    }
}
