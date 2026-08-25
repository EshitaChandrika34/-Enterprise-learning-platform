package com.infosys.learningservice.service;

import java.util.List;
import com.infosys.learningservice.dto.AssessmentDTO;



public interface AssessmentService {

    AssessmentDTO addAssessment(AssessmentDTO assessmentDTO);

    List<AssessmentDTO> getAllAssessments();

    AssessmentDTO getAssessmentById(Long id);

    List<AssessmentDTO> getAssessmentByEnrollmentId(Long enrollmentId);

    AssessmentDTO updateAssessment(Long id, AssessmentDTO assessmentDTO);

    void deleteAssessment(Long id);
}