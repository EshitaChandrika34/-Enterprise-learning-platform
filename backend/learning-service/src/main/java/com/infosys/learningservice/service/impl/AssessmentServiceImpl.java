package com.infosys.learningservice.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.infosys.learningservice.dto.AssessmentDTO;
import com.infosys.learningservice.entity.Assessment;
import com.infosys.learningservice.repository.AssessmentRepository;
import com.infosys.learningservice.service.AssessmentService;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository assessmentRepository;

    public AssessmentServiceImpl(
            AssessmentRepository assessmentRepository) {
        this.assessmentRepository = assessmentRepository;
    }

    @Override
    public AssessmentDTO addAssessment(
            AssessmentDTO assessmentDTO) {

        Assessment assessment = new Assessment();

        assessment.setEnrollmentId(
                assessmentDTO.getEnrollmentId());

        assessment.setAssessmentName(
                assessmentDTO.getAssessmentName());

        assessment.setScore(assessmentDTO.getScore());
        assessment.setResult(assessmentDTO.getResult());

        assessment.setAssessmentDate(
                assessmentDTO.getAssessmentDate());

        Assessment savedAssessment =
                assessmentRepository.save(assessment);

        return convertToDTO(savedAssessment);
    }

    @Override
    public List<AssessmentDTO> getAllAssessments() {

        return assessmentRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AssessmentDTO getAssessmentById(Long id) {

        Assessment assessment =
                assessmentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assessment not found with id: "
                                                + id));

        return convertToDTO(assessment);
    }

    @Override
    public List<AssessmentDTO> getAssessmentByEnrollmentId(Long enrollmentId) {

        return assessmentRepository
                .findByEnrollmentId(enrollmentId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AssessmentDTO updateAssessment(
            Long id,
            AssessmentDTO assessmentDTO) {

        Assessment assessment =
                assessmentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assessment not found with id: "
                                                + id));

        assessment.setEnrollmentId(
                assessmentDTO.getEnrollmentId());

        assessment.setAssessmentName(
                assessmentDTO.getAssessmentName());

        assessment.setScore(assessmentDTO.getScore());
        assessment.setResult(assessmentDTO.getResult());

        assessment.setAssessmentDate(
                assessmentDTO.getAssessmentDate());

        Assessment updatedAssessment =
                assessmentRepository.save(assessment);

        return convertToDTO(updatedAssessment);
    }

    @Override
    public void deleteAssessment(Long id) {

        Assessment assessment =
                assessmentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assessment not found with id: "
                                                + id));

        assessmentRepository.delete(assessment);
    }

    private AssessmentDTO convertToDTO(
            Assessment assessment) {

        AssessmentDTO dto = new AssessmentDTO();

        dto.setAssessmentId(
                assessment.getAssessmentId());

        dto.setEnrollmentId(
                assessment.getEnrollmentId());

        dto.setAssessmentName(
                assessment.getAssessmentName());

        dto.setScore(assessment.getScore());
        dto.setResult(assessment.getResult());

        dto.setAssessmentDate(
                assessment.getAssessmentDate());

        return dto;
    }
}