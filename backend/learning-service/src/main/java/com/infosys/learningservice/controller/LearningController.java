package com.infosys.learningservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.infosys.learningservice.dto.LearningCourseDTO;
import com.infosys.learningservice.service.LearningService;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/courses")

public class LearningController {

    @Autowired
    private LearningService learningService;

    @PostMapping
    public LearningCourseDTO addCourse(@RequestBody LearningCourseDTO courseDTO) {
        return learningService.addCourse(courseDTO);
    }

    @GetMapping
    public List<LearningCourseDTO> getAllCourses() {
        return learningService.getAllCourses();
    }

    @GetMapping("/{id}")
    public LearningCourseDTO getCourseById(@PathVariable Long id) {
        return learningService.getCourseById(id);
    }

    @PutMapping("/{id}")
    public LearningCourseDTO updateCourse(@PathVariable Long id,
                                          @RequestBody LearningCourseDTO courseDTO) {
        return learningService.updateCourse(id, courseDTO);
    }

    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable Long id) {
        learningService.deleteCourse(id);
        return "Course deleted successfully";
    }
}