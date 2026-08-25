package com.enterpriselearning.service.impl;

import com.enterpriselearning.dto.request.CareerGoalRequest;
import com.enterpriselearning.dto.request.CareerPathRequest;
import com.enterpriselearning.dto.response.*;
import com.enterpriselearning.entity.*;
import com.enterpriselearning.exception.ResourceNotFoundException;
import com.enterpriselearning.repository.*;
import com.enterpriselearning.service.CareerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareerServiceImpl implements CareerService {

    private final CareerGoalRepository careerGoalRepository;
    private final CareerPathRepository careerPathRepository;
    private final EmployeeSkillRepository employeeSkillRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CertificationRepository certificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CareerGoalResponse createCareerGoal(CareerGoalRequest request, String currentUserEmail) {
        User user;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));
        } else {
            user = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentUserEmail));
        }

        CareerGoal goal = CareerGoal.builder()
                .user(user)
                .targetRole(request.getTargetRole())
                .targetDate(request.getTargetDate())
                .status(request.getStatus() != null ? request.getStatus() : "IN_PROGRESS")
                .notes(request.getNotes())
                .build();

        CareerGoal saved = careerGoalRepository.save(goal);
        return mapToGoalResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CareerGoalResponse> getGoalsByEmployee(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + userId);
        }
        return careerGoalRepository.findByUserId(userId).stream()
                .map(this::mapToGoalResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CareerGoalResponse> getMyGoals(String currentUserEmail) {
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentUserEmail));
        return careerGoalRepository.findByUserId(user.getId()).stream()
                .map(this::mapToGoalResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CareerGoalResponse updateCareerGoal(Long id, CareerGoalRequest request) {
        CareerGoal goal = careerGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Career goal not found with id: " + id));

        goal.setTargetRole(request.getTargetRole());
        goal.setTargetDate(request.getTargetDate());
        goal.setStatus(request.getStatus());
        goal.setNotes(request.getNotes());

        CareerGoal updated = careerGoalRepository.save(goal);
        return mapToGoalResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCareerGoal(Long id) {
        CareerGoal goal = careerGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Career goal not found with id: " + id));
        careerGoalRepository.delete(goal);
    }

    @Override
    @Transactional
    public CareerPathResponse createCareerPath(CareerPathRequest request) {
        CareerPath careerPath = CareerPath.builder()
                .title(request.getTitle())
                .targetRole(request.getTargetRole())
                .department(request.getDepartment())
                .description(request.getDescription())
                .requiredSkills(request.getRequiredSkills())
                .recommendedCourses(request.getRecommendedCourses())
                .minExperienceYears(request.getMinExperienceYears())
                .build();

        CareerPath saved = careerPathRepository.save(careerPath);
        return mapToPathResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CareerPathResponse> getAllCareerPaths() {
        return careerPathRepository.findAll().stream()
                .map(this::mapToPathResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CareerPathResponse getCareerPathById(Long id) {
        CareerPath path = careerPathRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Career path not found with id: " + id));
        return mapToPathResponse(path);
    }

    @Override
    @Transactional(readOnly = true)
    public SkillRecommendationResponse getRecommendationsForEmployee(Long userId, String targetRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + userId));

        return buildRecommendations(user, targetRole);
    }

    @Override
    @Transactional(readOnly = true)
    public SkillRecommendationResponse getMyRecommendations(String currentUserEmail, String targetRole) {
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentUserEmail));

        return buildRecommendations(user, targetRole);
    }

    private SkillRecommendationResponse buildRecommendations(User user, String targetRole) {
        List<EmployeeSkill> employeeSkills = employeeSkillRepository.findByUserId(user.getId());
        List<String> currentSkillNames = employeeSkills.stream()
                .map(es -> es.getSkill().getName().trim())
                .collect(Collectors.toList());

        Set<String> requiredSkillSet = new LinkedHashSet<>();
        List<CareerPath> careerPaths;

        if (targetRole != null && !targetRole.trim().isEmpty()) {
            careerPaths = careerPathRepository.findByTargetRole(targetRole.trim());
            if (careerPaths.isEmpty()) {
                careerPaths = careerPathRepository.findAll();
            }
        } else {
            List<CareerGoal> goals = careerGoalRepository.findByUserId(user.getId());
            if (!goals.isEmpty()) {
                String goalRole = goals.get(0).getTargetRole();
                careerPaths = careerPathRepository.findByTargetRole(goalRole);
                if (careerPaths.isEmpty()) {
                    careerPaths = careerPathRepository.findAll();
                }
            } else {
                careerPaths = careerPathRepository.findAll();
            }
        }

        for (CareerPath cp : careerPaths) {
            if (cp.getRequiredSkills() != null && !cp.getRequiredSkills().isEmpty()) {
                String[] skills = cp.getRequiredSkills().split(",");
                for (String s : skills) {
                    if (!s.trim().isEmpty()) {
                        requiredSkillSet.add(s.trim());
                    }
                }
            }
        }

        List<String> missingSkills = new ArrayList<>();
        int matchedCount = 0;

        for (String reqSkill : requiredSkillSet) {
            boolean hasSkill = currentSkillNames.stream()
                    .anyMatch(cs -> cs.equalsIgnoreCase(reqSkill));
            if (hasSkill) {
                matchedCount++;
            } else {
                missingSkills.add(reqSkill);
            }
        }

        double matchPercentage = requiredSkillSet.isEmpty() ? 100.0 :
                Math.round(((double) matchedCount / requiredSkillSet.size()) * 100.0 * 100.0) / 100.0;

        List<CourseResponse> suggestedCourses = new ArrayList<>();
        List<Course> allCourses = courseRepository.findAll();

        for (Course course : allCourses) {
            for (String missing : missingSkills) {
                if (course.getTitle().toLowerCase().contains(missing.toLowerCase()) ||
                    (course.getDescription() != null && course.getDescription().toLowerCase().contains(missing.toLowerCase())) ||
                    (course.getCategory() != null && course.getCategory().toLowerCase().contains(missing.toLowerCase()))) {
                    
                    CourseResponse cr = CourseResponse.builder()
                            .id(course.getId())
                            .title(course.getTitle())
                            .description(course.getDescription())
                            .category(course.getCategory())
                            .level(course.getLevel())
                            .durationHours(course.getDurationHours())
                            .instructor(course.getInstructor())
                            .build();

                    if (suggestedCourses.stream().noneMatch(c -> c.getId().equals(cr.getId()))) {
                        suggestedCourses.add(cr);
                    }
                }
            }
        }

        return SkillRecommendationResponse.builder()
                .userId(user.getId())
                .employeeName(user.getFirstName() + " " + user.getLastName())
                .employeeId(user.getEmployeeId())
                .targetRole(targetRole != null ? targetRole : (careerPaths.isEmpty() ? "General Progression" : careerPaths.get(0).getTargetRole()))
                .currentSkills(currentSkillNames)
                .recommendedSkills(new ArrayList<>(requiredSkillSet))
                .missingSkills(missingSkills)
                .suggestedCourses(suggestedCourses)
                .matchPercentage(matchPercentage)
                .build();
    }

    private CareerGoalResponse mapToGoalResponse(CareerGoal goal) {
        return CareerGoalResponse.builder()
                .id(goal.getId())
                .userId(goal.getUser().getId())
                .employeeName(goal.getUser().getFirstName() + " " + goal.getUser().getLastName())
                .employeeId(goal.getUser().getEmployeeId())
                .targetRole(goal.getTargetRole())
                .targetDate(goal.getTargetDate())
                .status(goal.getStatus())
                .notes(goal.getNotes())
                .createdAt(goal.getCreatedAt())
                .updatedAt(goal.getUpdatedAt())
                .build();
    }

    private CareerPathResponse mapToPathResponse(CareerPath path) {
        List<String> reqSkills = path.getRequiredSkills() != null
                ? Arrays.stream(path.getRequiredSkills().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList())
                : List.of();

        List<String> recCourses = path.getRecommendedCourses() != null
                ? Arrays.stream(path.getRecommendedCourses().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList())
                : List.of();

        return CareerPathResponse.builder()
                .id(path.getId())
                .title(path.getTitle())
                .targetRole(path.getTargetRole())
                .department(path.getDepartment())
                .description(path.getDescription())
                .requiredSkills(path.getRequiredSkills())
                .requiredSkillList(reqSkills)
                .recommendedCourses(path.getRecommendedCourses())
                .recommendedCourseList(recCourses)
                .minExperienceYears(path.getMinExperienceYears())
                .createdAt(path.getCreatedAt())
                .build();
    }

    // --- Career Progress & Readiness Calculation ---

    @Override
    @Transactional(readOnly = true)
    public CareerProgressResponse calculateCareerProgress(Long userId, Long pathId) {
        User employee = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + userId));

        CareerPath path;
        if (pathId != null) {
            path = careerPathRepository.findById(pathId)
                    .orElseThrow(() -> new ResourceNotFoundException("Career path not found with id: " + pathId));
        } else {
            // Find path matching user's active career goal or department
            List<CareerGoal> goals = careerGoalRepository.findByUserId(userId);
            if (!goals.isEmpty()) {
                String targetRole = goals.get(0).getTargetRole();
                path = careerPathRepository.findByTargetRoleIgnoreCase(targetRole)
                        .stream().findFirst()
                        .orElse(careerPathRepository.findAll().stream().findFirst()
                                .orElseThrow(() -> new ResourceNotFoundException("No career paths configured")));
            } else {
                path = careerPathRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("No career paths configured"));
            }
        }

        CareerGoal goal = careerGoalRepository.findByUserId(userId).stream()
                .filter(g -> g.getTargetRole().equalsIgnoreCase(path.getTargetRole()))
                .findFirst()
                .orElse(null);

        return computeCareerProgress(employee, path, goal);
    }

    @Override
    @Transactional(readOnly = true)
    public CareerProgressResponse calculateMyCareerProgress(String currentUserEmail, Long pathId) {
        User employee = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentUserEmail));
        return calculateCareerProgress(employee.getId(), pathId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CareerProgressResponse> getAllCareerProgressForEmployee(Long userId) {
        User employee = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + userId));

        List<CareerPath> paths = careerPathRepository.findAll();
        List<CareerGoal> goals = careerGoalRepository.findByUserId(userId);

        return paths.stream()
                .map(p -> {
                    CareerGoal matchingGoal = goals.stream()
                            .filter(g -> g.getTargetRole().equalsIgnoreCase(p.getTargetRole()))
                            .findFirst()
                            .orElse(null);
                    return computeCareerProgress(employee, p, matchingGoal);
                })
                .sorted(Comparator.comparingDouble(CareerProgressResponse::getOverallProgressPercentage).reversed())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CareerProgressResponse> getMyCareerProgressForAllGoals(String currentUserEmail) {
        User employee = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentUserEmail));
        return getAllCareerProgressForEmployee(employee.getId());
    }

    private CareerProgressResponse computeCareerProgress(User employee, CareerPath path, CareerGoal goal) {
        List<EmployeeSkill> empSkills = employeeSkillRepository.findByUserId(employee.getId());
        Map<String, EmployeeSkill> empSkillMap = new HashMap<>();
        for (EmployeeSkill es : empSkills) {
            empSkillMap.put(es.getSkill().getName().trim().toLowerCase(), es);
        }

        // 1. Skill Mastery Factor (40% Weight)
        List<String> reqSkills = parseCommaList(path.getRequiredSkills());
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();
        double earnedSkillPoints = 0.0;

        for (String reqSkill : reqSkills) {
            String lower = reqSkill.toLowerCase();
            if (empSkillMap.containsKey(lower)) {
                EmployeeSkill es = empSkillMap.get(lower);
                double weight = getSkillWeight(es.getProficiencyLevel());
                earnedSkillPoints += weight;
                matchedSkills.add(reqSkill);
            } else {
                missingSkills.add(reqSkill);
            }
        }

        double skillProgressPercentage = reqSkills.isEmpty() ? 100.0 : Math.min(100.0, (earnedSkillPoints / reqSkills.size()) * 100.0);
        double skillContributionScore = Math.round(skillProgressPercentage * 0.40 * 10.0) / 10.0;

        // 2. Learning & Course Completion Factor (30% Weight)
        List<String> recCourses = parseCommaList(path.getRecommendedCourses());
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(employee.getId());
        List<String> completedCourses = new ArrayList<>();
        List<String> pendingCourses = new ArrayList<>();
        double totalCourseProgress = 0.0;

        for (String recCourse : recCourses) {
            String courseTitleLower = recCourse.toLowerCase();
            Optional<Enrollment> enrollmentOpt = enrollments.stream()
                    .filter(e -> e.getCourse().getTitle().toLowerCase().contains(courseTitleLower) ||
                                 courseTitleLower.contains(e.getCourse().getTitle().toLowerCase()))
                    .findFirst();

            if (enrollmentOpt.isPresent()) {
                Enrollment enr = enrollmentOpt.get();
                if (enr.getStatus() == EnrollmentStatus.COMPLETED) {
                    completedCourses.add(recCourse);
                    totalCourseProgress += 1.0;
                } else {
                    double progress = enr.getProgressPercentage() != null ? enr.getProgressPercentage() / 100.0 : 0.0;
                    totalCourseProgress += progress;
                    pendingCourses.add(recCourse + " (" + (int)(progress * 100) + "% completed)");
                }
            } else {
                pendingCourses.add(recCourse + " (Not enrolled)");
            }
        }

        double courseProgressPercentage = recCourses.isEmpty() ? 100.0 : Math.min(100.0, (totalCourseProgress / recCourses.size()) * 100.0);
        double courseContributionScore = Math.round(courseProgressPercentage * 0.30 * 10.0) / 10.0;

        // 3. Certifications Factor (15% Weight)
        List<Certification> certs = certificationRepository.findByUserId(employee.getId());
        int certCount = certs.size();
        double certProgressPercentage = Math.min(100.0, certCount * 50.0); // 2 certs = 100%
        double certContributionScore = Math.round(certProgressPercentage * 0.15 * 10.0) / 10.0;

        // 4. Experience & Tenure Factor (15% Weight)
        int currentExp = empSkills.stream()
                .mapToInt(es -> es.getYearsOfExperience() != null ? es.getYearsOfExperience() : 0)
                .max()
                .orElse(0);

        int reqExp = path.getMinExperienceYears() != null ? path.getMinExperienceYears() : 0;
        double expRatio = reqExp <= 0 ? 1.0 : Math.min(1.0, (double) currentExp / reqExp);
        double expProgressPercentage = Math.round(expRatio * 100.0 * 10.0) / 10.0;
        double expContributionScore = Math.round(expRatio * 15.0 * 10.0) / 10.0;

        // Overall Score (0 - 100%)
        double overallScore = Math.min(100.0, Math.round((skillContributionScore + courseContributionScore + certContributionScore + expContributionScore) * 10.0) / 10.0);
        String stage = getReadinessStage(overallScore);

        // Actionable Milestones
        List<String> milestones = new ArrayList<>();
        if (!missingSkills.isEmpty()) {
            milestones.add("Acquire competencies in missing skill(s): " + String.join(", ", missingSkills) + ".");
        }
        if (!pendingCourses.isEmpty()) {
            milestones.add("Complete recommended learning track: " + String.join(", ", pendingCourses) + ".");
        }
        if (certCount < 2) {
            milestones.add("Earn " + (2 - certCount) + " more industry certification(s) in " + path.getDepartment() + ".");
        }
        if (currentExp < reqExp) {
            milestones.add("Accumulate " + (reqExp - currentExp) + " more year(s) of specialized experience.");
        }
        if (milestones.isEmpty()) {
            milestones.add("Congratulations! All target competencies, learning paths, and tenure milestones are fully completed.");
        }

        return CareerProgressResponse.builder()
                .careerGoalId(goal != null ? goal.getId() : null)
                .targetRole(path.getTargetRole())
                .careerPathId(path.getId())
                .careerPathTitle(path.getTitle())
                .department(path.getDepartment())
                .employeeId(employee.getId())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .employeeEmail(employee.getEmail())
                .overallProgressPercentage(overallScore)
                .readinessStage(stage)
                .skillProgressPercentage(Math.round(skillProgressPercentage * 10.0) / 10.0)
                .skillContributionScore(skillContributionScore)
                .skillsMatchedCount(matchedSkills.size())
                .skillsTotalCount(reqSkills.size())
                .matchedSkills(matchedSkills)
                .missingSkills(missingSkills)
                .courseProgressPercentage(Math.round(courseProgressPercentage * 10.0) / 10.0)
                .courseContributionScore(courseContributionScore)
                .coursesCompletedCount(completedCourses.size())
                .coursesTotalCount(recCourses.size())
                .completedCourses(completedCourses)
                .pendingCourses(pendingCourses)
                .certificationProgressPercentage(Math.round(certProgressPercentage * 10.0) / 10.0)
                .certificationContributionScore(certContributionScore)
                .certificationsCount(certCount)
                .experienceProgressPercentage(expProgressPercentage)
                .experienceContributionScore(expContributionScore)
                .currentExperienceYears(currentExp)
                .requiredExperienceYears(reqExp)
                .actionableMilestones(milestones)
                .targetDate(goal != null ? goal.getTargetDate() : null)
                .goalStatus(goal != null ? goal.getStatus() : "ACTIVE")
                .build();
    }

    private double getSkillWeight(ProficiencyLevel level) {
        if (level == null) return 0.6;
        return switch (level) {
            case EXPERT -> 1.1;
            case ADVANCED -> 1.0;
            case INTERMEDIATE -> 0.85;
            case BEGINNER -> 0.6;
        };
    }

    private String getReadinessStage(double score) {
        if (score >= 85.0) return "READY_FOR_PROMOTION";
        if (score >= 70.0) return "NEAR_READINESS";
        if (score >= 45.0) return "DEVELOPING";
        return "EARLY_STAGE";
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
}
