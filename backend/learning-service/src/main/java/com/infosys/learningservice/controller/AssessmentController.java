package com.infosys.learningservice.controller;

import java.util.List;

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

import com.infosys.learningservice.dto.AssessmentDTO;
import com.infosys.learningservice.service.AssessmentService;

@RestController
@RequestMapping("/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(
            AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping
    public ResponseEntity<AssessmentDTO> addAssessment(
            @RequestBody AssessmentDTO assessmentDTO) {

        AssessmentDTO savedAssessment =
                assessmentService.addAssessment(assessmentDTO);

        return new ResponseEntity<>(
                savedAssessment,
                HttpStatus.CREATED);
    }

    @GetMapping
    public List<AssessmentDTO> getAllAssessments() {
        return assessmentService.getAllAssessments();
    }

    @GetMapping("/{id}")
    public AssessmentDTO getAssessmentById(
            @PathVariable Long id) {

        return assessmentService.getAssessmentById(id);
    }

    @GetMapping("/enrollment/{enrollmentId}")
    public List<AssessmentDTO> getByEnrollmentId(
            @PathVariable Long enrollmentId) {

        return assessmentService
                .getAssessmentByEnrollmentId(enrollmentId);
    }

    @PutMapping("/{id}")
    public AssessmentDTO updateAssessment(
            @PathVariable Long id,
            @RequestBody AssessmentDTO assessmentDTO) {

        return assessmentService
                .updateAssessment(id, assessmentDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAssessment(
            @PathVariable Long id) {

        assessmentService.deleteAssessment(id);

        return ResponseEntity.ok(
                "Assessment deleted successfully");
    }
}