package com.infosys.learningservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.learningservice.dto.CourseProgressDTO;
import com.infosys.learningservice.service.CourseProgressService;

@RestController
@RequestMapping("/progress")
@CrossOrigin(origins = "http://localhost:4200")
public class CourseProgressController {

    private final CourseProgressService courseProgressService;

    public CourseProgressController(
            CourseProgressService courseProgressService) {

        this.courseProgressService =
                courseProgressService;
    }

    @PostMapping
    public ResponseEntity<CourseProgressDTO> addProgress(
            @RequestBody CourseProgressDTO progressDTO) {

        CourseProgressDTO savedProgress =
                courseProgressService.addProgress(progressDTO);

        return new ResponseEntity<>(
                savedProgress,
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public List<CourseProgressDTO> getAllProgress() {

        return courseProgressService.getAllProgress();
    }

    @GetMapping("/{id}")
    public CourseProgressDTO getProgressById(
            @PathVariable Long id) {

        return courseProgressService
                .getProgressById(id);
    }

    @GetMapping("/enrollment/{enrollmentId}")
    public List<CourseProgressDTO> getProgressByEnrollmentId(
            @PathVariable Long enrollmentId) {

        return courseProgressService
                .getProgressByEnrollmentId(enrollmentId);
    }

    @PutMapping("/{id}")
    public CourseProgressDTO updateProgress(
            @PathVariable Long id,
            @RequestBody CourseProgressDTO progressDTO) {

        return courseProgressService
                .updateProgress(id, progressDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProgress(
            @PathVariable Long id) {

        courseProgressService.deleteProgress(id);

        return ResponseEntity.ok(
                "Course Progress deleted successfully"
        );
    }
}