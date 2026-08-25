package com.infosys.skillmicroservice.service;

import java.util.List;
import java.util.UUID;

import com.infosys.skillmicroservice.entity.Assessment;

public interface AssessmentService {

    Assessment addAssessment(Assessment assessment);

    List<Assessment> getAllAssessments();

    Assessment getAssessmentById(UUID assessmentId);

    List<Assessment> getAssessmentsByUserId(UUID userId);

    Assessment updateAssessment(
            UUID assessmentId,
            Assessment assessment);

    void deleteAssessment(UUID assessmentId);
}