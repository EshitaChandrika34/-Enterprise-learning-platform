package com.infosys.learningservice.controller;

import java.util.List;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.learningservice.dto.CourseContentDTO;
import com.infosys.learningservice.service.CourseContentService;

@RestController
@RequestMapping("/course-contents")
public class CourseContentController {

    private final CourseContentService courseContentService;

    public CourseContentController(
            CourseContentService courseContentService) {
        this.courseContentService = courseContentService;
    }

    @PostMapping
    public ResponseEntity<CourseContentDTO> addCourseContent(
            @Valid @RequestBody CourseContentDTO dto) {

        CourseContentDTO savedContent =
                courseContentService.addCourseContent(dto);

        return new ResponseEntity<>(
                savedContent,
                HttpStatus.CREATED);
    }

    @GetMapping
    public List<CourseContentDTO> getAllCourseContents() {
        return courseContentService.getAllCourseContents();
    }

    @GetMapping("/{id}")
    public CourseContentDTO getCourseContentById(
            @PathVariable Long id) {

        return courseContentService
                .getCourseContentById(id);
    }

    @GetMapping("/course/{courseId}")
    public List<CourseContentDTO> getByCourseId(
            @PathVariable Long courseId) {

        return courseContentService
                .getCourseContentsByCourseId(courseId);
    }

    @PutMapping("/{id}")
    public CourseContentDTO updateCourseContent(
            @PathVariable Long id,
            @Valid @RequestBody CourseContentDTO dto) {

        return courseContentService
                .updateCourseContent(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourseContent(
            @PathVariable Long id) {

        courseContentService.deleteCourseContent(id);

        return ResponseEntity.ok(
                "Course content deleted successfully");
    }
}