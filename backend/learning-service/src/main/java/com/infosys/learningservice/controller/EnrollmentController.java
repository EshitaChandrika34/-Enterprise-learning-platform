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

import com.infosys.learningservice.dto.EnrollmentDTO;
import com.infosys.learningservice.service.EnrollmentService;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(
            EnrollmentService enrollmentService) {

        this.enrollmentService = enrollmentService;
    }


    // ==========================================
    // ADD ENROLLMENT
    // ==========================================

    @PostMapping
    public ResponseEntity<EnrollmentDTO> addEnrollment(
            @RequestBody EnrollmentDTO dto) {

        EnrollmentDTO saved =
                enrollmentService.addEnrollment(dto);

        return new ResponseEntity<>(
                saved,
                HttpStatus.CREATED
        );
    }


    // ==========================================
    // GET ALL ENROLLMENTS
    // ==========================================

    @GetMapping
    public ResponseEntity<List<EnrollmentDTO>>
    getAllEnrollments() {

        return ResponseEntity.ok(
                enrollmentService.getAllEnrollments()
        );
    }


    // ==========================================
    // GET ENROLLMENT BY ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentDTO>
    getEnrollmentById(
            @PathVariable Long id) {

        EnrollmentDTO enrollment =
                enrollmentService.getEnrollmentById(id);

        return ResponseEntity.ok(enrollment);
    }


    // ==========================================
    // GET ENROLLMENTS BY EMPLOYEE ID
    // ==========================================

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EnrollmentDTO>>
    getEnrollmentsByEmployeeId(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                enrollmentService
                        .getEnrollmentsByEmployeeId(employeeId)
        );
    }


    // ==========================================
    // UPDATE ENROLLMENT
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<EnrollmentDTO>
    updateEnrollment(
            @PathVariable Long id,
            @RequestBody EnrollmentDTO dto) {

        return ResponseEntity.ok(
                enrollmentService.updateEnrollment(
                        id,
                        dto
                )
        );
    }


    // ==========================================
    // DELETE ENROLLMENT
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String>
    deleteEnrollment(
            @PathVariable Long id) {

        enrollmentService.deleteEnrollment(id);

        return ResponseEntity.ok(
                "Enrollment deleted successfully."
        );
    }
}