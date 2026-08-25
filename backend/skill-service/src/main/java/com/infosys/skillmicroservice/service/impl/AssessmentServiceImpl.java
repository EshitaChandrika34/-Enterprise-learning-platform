package com.infosys.skillmicroservice.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.infosys.skillmicroservice.entity.Assessment;
import com.infosys.skillmicroservice.entity.Skill;
import com.infosys.skillmicroservice.repository.AssessmentRepository;
import com.infosys.skillmicroservice.repository.SkillRepository;
import com.infosys.skillmicroservice.service.AssessmentService;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final SkillRepository skillRepository;

    public AssessmentServiceImpl(
            AssessmentRepository assessmentRepository,
            SkillRepository skillRepository) {

        this.assessmentRepository = assessmentRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public Assessment addAssessment(Assessment assessment) {

        UUID skillId = assessment.getSkill().getSkillId();

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Skill not found with ID: " + skillId));

        assessment.setSkill(skill);

        if (assessment.getScore() != null) {
            assessment.setPassed(assessment.getScore() >= 60);
        }

        return assessmentRepository.save(assessment);
    }

    @Override
    public List<Assessment> getAllAssessments() {
        return assessmentRepository.findAll();
    }

    @Override
    public Assessment getAssessmentById(UUID assessmentId) {

        return assessmentRepository.findById(assessmentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Assessment not found with ID: "
                                        + assessmentId));
    }

    @Override
    public List<Assessment> getAssessmentsByUserId(UUID userId) {
        return assessmentRepository.findByUserId(userId);
    }

    @Override
    public Assessment updateAssessment(
            UUID assessmentId,
            Assessment newAssessment) {

        Assessment existingAssessment =
                getAssessmentById(assessmentId);

        if (newAssessment.getSkill() != null
                && newAssessment.getSkill().getSkillId() != null) {

            UUID skillId =
                    newAssessment.getSkill().getSkillId();

            Skill skill = skillRepository.findById(skillId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Skill not found with ID: "
                                            + skillId));

            existingAssessment.setSkill(skill);
        }

        existingAssessment.setUserId(
                newAssessment.getUserId());

        existingAssessment.setScore(
                newAssessment.getScore());

        existingAssessment.setVerified(
                newAssessment.getVerified());

        if (newAssessment.getScore() != null) {
            existingAssessment.setPassed(
                    newAssessment.getScore() >= 60);
        }

        return assessmentRepository.save(existingAssessment);
    }

    @Override
    public void deleteAssessment(UUID assessmentId) {

        Assessment assessment =
                getAssessmentById(assessmentId);

        assessmentRepository.delete(assessment);
    }
}