package com.enterpriselearning.service.impl;

import com.enterpriselearning.dto.request.PromotionCriteriaRequest;
import com.enterpriselearning.dto.response.PromotionCriteriaResponse;
import com.enterpriselearning.dto.response.PromotionEvaluationResponse;
import com.enterpriselearning.entity.*;
import com.enterpriselearning.exception.ResourceNotFoundException;
import com.enterpriselearning.repository.*;
import com.enterpriselearning.service.PromotionCriteriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionCriteriaServiceImpl implements PromotionCriteriaService {

    private final PromotionCriteriaRepository repository;
    private final UserRepository userRepository;
    private final EmployeeSkillRepository employeeSkillRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CertificationRepository certificationRepository;

    // --- Preserved Entity-based CRUD ---

    @Override
    @Transactional
    public PromotionCriteria create(PromotionCriteria criteria) {
        return repository.save(criteria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionCriteria> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionCriteria> getActive() {
        return repository.findByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionCriteria getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion criteria not found with id: " + id));
    }

    @Override
    @Transactional
    public PromotionCriteria update(Long id, PromotionCriteria criteria) {
        PromotionCriteria existing = getById(id);

        existing.setName(criteria.getName());
        existing.setDescription(criteria.getDescription());
        existing.setMinimumScore(criteria.getMinimumScore());
        existing.setTargetRole(criteria.getTargetRole());
        existing.setRequiredDepartment(criteria.getRequiredDepartment());
        existing.setMinExperienceYears(criteria.getMinExperienceYears());
        existing.setRequiredSkills(criteria.getRequiredSkills());
        existing.setMinCompletedCourses(criteria.getMinCompletedCourses());
        existing.setMinCertifications(criteria.getMinCertifications());
        if (criteria.getActive() != null) {
            existing.setActive(criteria.getActive());
        }

        return repository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        PromotionCriteria existing = getById(id);
        repository.delete(existing);
    }

    // --- DTO-based CRUD ---

    @Override
    @Transactional
    public PromotionCriteriaResponse createCriteria(PromotionCriteriaRequest request) {
        PromotionCriteria criteria = PromotionCriteria.builder()
                .name(request.getName())
                .description(request.getDescription())
                .minimumScore(request.getMinimumScore())
                .targetRole(request.getTargetRole())
                .requiredDepartment(request.getRequiredDepartment())
                .minExperienceYears(request.getMinExperienceYears())
                .requiredSkills(request.getRequiredSkills())
                .minCompletedCourses(request.getMinCompletedCourses())
                .minCertifications(request.getMinCertifications())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        PromotionCriteria saved = repository.save(criteria);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionCriteriaResponse> getAllCriteria() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionCriteriaResponse> getActiveCriteria() {
        return repository.findByActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionCriteriaResponse getCriteriaById(Long id) {
        PromotionCriteria criteria = getById(id);
        return mapToResponse(criteria);
    }

    @Override
    @Transactional
    public PromotionCriteriaResponse updateCriteria(Long id, PromotionCriteriaRequest request) {
        PromotionCriteria existing = getById(id);

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setMinimumScore(request.getMinimumScore());
        existing.setTargetRole(request.getTargetRole());
        existing.setRequiredDepartment(request.getRequiredDepartment());
        existing.setMinExperienceYears(request.getMinExperienceYears());
        existing.setRequiredSkills(request.getRequiredSkills());
        existing.setMinCompletedCourses(request.getMinCompletedCourses());
        existing.setMinCertifications(request.getMinCertifications());
        if (request.getActive() != null) {
            existing.setActive(request.getActive());
        }

        PromotionCriteria saved = repository.save(existing);
        return mapToResponse(saved);
    }

    // --- Evaluation Engine Methods ---

    @Override
    @Transactional(readOnly = true)
    public PromotionEvaluationResponse evaluateEmployee(Long employeeId, Long criteriaId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        PromotionCriteria criteria = getById(criteriaId);
        return runEvaluation(employee, criteria);
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionEvaluationResponse evaluateMyEligibility(String userEmail, Long criteriaId) {
        User employee = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + userEmail));

        PromotionCriteria criteria = getById(criteriaId);
        return runEvaluation(employee, criteria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionEvaluationResponse> evaluateEmployeeAgainstAll(Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        List<PromotionCriteria> allActive = repository.findByActiveTrue();
        return allActive.stream()
                .map(criteria -> runEvaluation(employee, criteria))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionEvaluationResponse> evaluateMyEligibilityAgainstAll(String userEmail) {
        User employee = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + userEmail));

        List<PromotionCriteria> allActive = repository.findByActiveTrue();
        return allActive.stream()
                .map(criteria -> runEvaluation(employee, criteria))
                .collect(Collectors.toList());
    }

    private PromotionEvaluationResponse runEvaluation(User employee, PromotionCriteria criteria) {
        List<String> feedback = new ArrayList<>();

        // 1. Department Evaluation
        boolean departmentSatisfied = true;
        if (criteria.getRequiredDepartment() != null && !criteria.getRequiredDepartment().trim().isEmpty()) {
            departmentSatisfied = employee.getDepartment() != null &&
                    employee.getDepartment().equalsIgnoreCase(criteria.getRequiredDepartment().trim());
            if (!departmentSatisfied) {
                feedback.add("Department mismatch: Required department is '" + criteria.getRequiredDepartment() +
                        "', but current department is '" + employee.getDepartment() + "'.");
            }
        }

        // 2. Skills Evaluation (40% Weight)
        List<String> reqSkillsList = parseCommaList(criteria.getRequiredSkills());
        List<EmployeeSkill> employeeSkills = employeeSkillRepository.findByUserId(employee.getId());
        Set<String> employeeSkillNames = employeeSkills.stream()
                .map(es -> es.getSkill().getName().trim().toLowerCase())
                .collect(Collectors.toSet());

        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String reqSkill : reqSkillsList) {
            if (employeeSkillNames.contains(reqSkill.toLowerCase())) {
                matchedSkills.add(reqSkill);
            } else {
                missingSkills.add(reqSkill);
            }
        }

        double skillsScore;
        boolean skillsSatisfied;
        if (reqSkillsList.isEmpty()) {
            skillsScore = 40.0;
            skillsSatisfied = true;
        } else {
            double matchRatio = (double) matchedSkills.size() / reqSkillsList.size();
            skillsScore = Math.round(matchRatio * 40.0 * 100.0) / 100.0;
            skillsSatisfied = missingSkills.isEmpty();
            if (!skillsSatisfied) {
                feedback.add("Missing required skills: " + String.join(", ", missingSkills) + ".");
            } else {
                feedback.add("All " + matchedSkills.size() + " required skills satisfied.");
            }
        }

        // 3. Experience Evaluation (25% Weight)
        int currentExp = employeeSkills.stream()
                .mapToInt(es -> es.getYearsOfExperience() != null ? es.getYearsOfExperience() : 0)
                .max()
                .orElse(0);

        int reqExp = criteria.getMinExperienceYears() != null ? criteria.getMinExperienceYears() : 0;
        double expScore;
        boolean experienceSatisfied;
        if (reqExp <= 0) {
            expScore = 25.0;
            experienceSatisfied = true;
        } else {
            double expRatio = Math.min(1.0, (double) currentExp / reqExp);
            expScore = Math.round(expRatio * 25.0 * 100.0) / 100.0;
            experienceSatisfied = currentExp >= reqExp;
            if (!experienceSatisfied) {
                feedback.add("Experience gap: Has " + currentExp + " years, requires at least " + reqExp + " years.");
            } else {
                feedback.add("Experience requirement satisfied (" + currentExp + " years).");
            }
        }

        // 4. Completed Courses Evaluation (20% Weight)
        long completedCourses = enrollmentRepository.findByUserId(employee.getId()).stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED)
                .count();

        int reqCourses = criteria.getMinCompletedCourses() != null ? criteria.getMinCompletedCourses() : 0;
        double coursesScore;
        boolean coursesSatisfied;
        if (reqCourses <= 0) {
            coursesScore = 20.0;
            coursesSatisfied = true;
        } else {
            double courseRatio = Math.min(1.0, (double) completedCourses / reqCourses);
            coursesScore = Math.round(courseRatio * 20.0 * 100.0) / 100.0;
            coursesSatisfied = completedCourses >= reqCourses;
            if (!coursesSatisfied) {
                feedback.add("Course completion gap: Completed " + completedCourses + " courses, requires " + reqCourses + ".");
            } else {
                feedback.add("Course completion requirement satisfied (" + completedCourses + " completed).");
            }
        }

        // 5. Certifications Evaluation (15% Weight)
        long certCount = certificationRepository.findByUserId(employee.getId()).size();
        int reqCerts = criteria.getMinCertifications() != null ? criteria.getMinCertifications() : 0;
        double certScore;
        boolean certificationsSatisfied;
        if (reqCerts <= 0) {
            certScore = 15.0;
            certificationsSatisfied = true;
        } else {
            double certRatio = Math.min(1.0, (double) certCount / reqCerts);
            certScore = Math.round(certRatio * 15.0 * 100.0) / 100.0;
            certificationsSatisfied = certCount >= reqCerts;
            if (!certificationsSatisfied) {
                feedback.add("Certification gap: Holds " + certCount + " certifications, requires " + reqCerts + ".");
            } else {
                feedback.add("Certification requirement satisfied (" + certCount + " certifications held).");
            }
        }

        // Total Score & Eligibility
        double overallScore = Math.round((skillsScore + expScore + coursesScore + certScore) * 100.0) / 100.0;
        int minScore = criteria.getMinimumScore() != null ? criteria.getMinimumScore() : 70;
        boolean eligible = overallScore >= minScore && departmentSatisfied;

        if (eligible) {
            feedback.add(0, "ELIGIBLE: Employee meets or exceeds the promotion qualification score of " + minScore + "% with an overall score of " + overallScore + "%.");
        } else {
            feedback.add(0, "NOT YET ELIGIBLE: Current overall score is " + overallScore + "%, required minimum is " + minScore + "%.");
        }

        return PromotionEvaluationResponse.builder()
                .criteriaId(criteria.getId())
                .criteriaName(criteria.getName())
                .targetRole(criteria.getTargetRole())
                .minimumRequiredScore(minScore)
                .employeeId(employee.getId())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .employeeCode(employee.getEmployeeId())
                .department(employee.getDepartment())
                .eligible(eligible)
                .overallScore(overallScore)
                .skillsSatisfied(skillsSatisfied)
                .skillsScore(skillsScore)
                .requiredSkills(reqSkillsList)
                .matchedSkills(matchedSkills)
                .missingSkills(missingSkills)
                .experienceSatisfied(experienceSatisfied)
                .experienceScore(expScore)
                .currentExperienceYears(currentExp)
                .requiredExperienceYears(reqExp)
                .coursesSatisfied(coursesSatisfied)
                .coursesScore(coursesScore)
                .completedCoursesCount(completedCourses)
                .requiredCompletedCourses(reqCourses)
                .certificationsSatisfied(certificationsSatisfied)
                .certificationsScore(certScore)
                .certificationsCount(certCount)
                .requiredCertifications(reqCerts)
                .departmentSatisfied(departmentSatisfied)
                .requiredDepartment(criteria.getRequiredDepartment())
                .feedback(feedback)
                .build();
    }

    private List<String> parseCommaList(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private PromotionCriteriaResponse mapToResponse(PromotionCriteria c) {
        return PromotionCriteriaResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .minimumScore(c.getMinimumScore())
                .targetRole(c.getTargetRole())
                .requiredDepartment(c.getRequiredDepartment())
                .minExperienceYears(c.getMinExperienceYears())
                .requiredSkills(c.getRequiredSkills())
                .requiredSkillList(parseCommaList(c.getRequiredSkills()))
                .minCompletedCourses(c.getMinCompletedCourses())
                .minCertifications(c.getMinCertifications())
                .active(c.getActive())
                .build();
    }
}
