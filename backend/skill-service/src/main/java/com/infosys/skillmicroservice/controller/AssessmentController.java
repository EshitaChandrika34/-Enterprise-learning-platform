package com.infosys.skillmicroservice.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.skillmicroservice.entity.Assessment;
import com.infosys.skillmicroservice.service.AssessmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(
            AssessmentService assessmentService) {

        this.assessmentService = assessmentService;
    }

    @PostMapping
    public Assessment addAssessment(
            @Valid @RequestBody Assessment assessment) {

        return assessmentService.addAssessment(assessment);
    }

    @GetMapping
    public List<Assessment> getAllAssessments() {
        return assessmentService.getAllAssessments();
    }

    @GetMapping("/{assessmentId}")
    public Assessment getAssessmentById(
            @PathVariable UUID assessmentId) {

        return assessmentService
                .getAssessmentById(assessmentId);
    }

    @GetMapping("/user/{userId}")
    public List<Assessment> getAssessmentsByUserId(
            @PathVariable UUID userId) {

        return assessmentService
                .getAssessmentsByUserId(userId);
    }

    @PutMapping("/{assessmentId}")
    public Assessment updateAssessment(
            @PathVariable UUID assessmentId,
            @Valid @RequestBody Assessment assessment) {

        return assessmentService.updateAssessment(
                assessmentId,
                assessment);
    }

    @DeleteMapping("/{assessmentId}")
    public String deleteAssessment(
            @PathVariable UUID assessmentId) {

        assessmentService.deleteAssessment(assessmentId);

        return "Assessment deleted successfully";
    }
}