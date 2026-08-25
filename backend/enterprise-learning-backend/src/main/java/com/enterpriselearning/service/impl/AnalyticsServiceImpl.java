package com.enterpriselearning.service.impl;

import com.enterpriselearning.dto.response.*;
import com.enterpriselearning.entity.*;
import com.enterpriselearning.repository.*;
import com.enterpriselearning.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final EmployeeSkillRepository employeeSkillRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CertificationRepository certificationRepository;

    @Override
    @Transactional(readOnly = true)
    public AnalyticsDashboardResponse getDashboardAnalytics() {
        return AnalyticsDashboardResponse.builder()
                .employeeStats(getEmployeeStats())
                .skillStats(getSkillStats())
                .learningStats(getLearningStats())
                .certificationStats(getCertificationStats())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeStatsResponse getEmployeeStats() {
        long totalEmployees = userRepository.countByRole(Role.EMPLOYEE);
        long totalManagers = userRepository.countByRole(Role.HR);
        long totalAdmins = userRepository.countByRole(Role.ADMIN);

        List<User> allUsers = userRepository.findAll();
        Map<String, Long> employeesByDepartment = allUsers.stream()
                .filter(u -> u.getDepartment() != null && !u.getDepartment().isEmpty())
                .collect(Collectors.groupingBy(User::getDepartment, Collectors.counting()));

        return EmployeeStatsResponse.builder()
                .totalEmployees(totalEmployees)
                .totalManagers(totalManagers)
                .totalAdmins(totalAdmins)
                .employeesByDepartment(employeesByDepartment)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SkillStatsResponse getSkillStats() {
        long totalSkills = skillRepository.count();
        long totalSkillAssignments = employeeSkillRepository.count();

        Map<String, Long> proficiencyDistribution = new LinkedHashMap<>();
        for (ProficiencyLevel level : ProficiencyLevel.values()) {
            proficiencyDistribution.put(level.name(), employeeSkillRepository.countByProficiencyLevel(level));
        }

        List<Object[]> topSkillsData = employeeSkillRepository.countEmployeesBySkill();
        Map<String, Long> topSkills = new LinkedHashMap<>();
        for (Object[] row : topSkillsData) {
            if (topSkills.size() >= 10) break;
            topSkills.put((String) row[0], ((Number) row[1]).longValue());
        }

        return SkillStatsResponse.builder()
                .totalSkills(totalSkills)
                .totalSkillAssignments(totalSkillAssignments)
                .proficiencyDistribution(proficiencyDistribution)
                .topSkills(topSkills)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public LearningStatsResponse getLearningStats() {
        long totalCourses = courseRepository.count();
        long totalEnrollments = enrollmentRepository.count();
        long completedEnrollments = enrollmentRepository.countByStatus(EnrollmentStatus.COMPLETED);
        long inProgressEnrollments = enrollmentRepository.countByStatus(EnrollmentStatus.IN_PROGRESS);

        Double avgProgress = enrollmentRepository.getAverageProgressPercentage();
        double averageProgressPercentage = (avgProgress != null) ? Math.round(avgProgress * 100.0) / 100.0 : 0.0;

        List<Object[]> topEnrollmentData = enrollmentRepository.countEnrollmentsByCourse();
        Map<String, Long> topCourses = new LinkedHashMap<>();
        for (Object[] row : topEnrollmentData) {
            if (topCourses.size() >= 10) break;
            topCourses.put((String) row[0], ((Number) row[1]).longValue());
        }

        return LearningStatsResponse.builder()
                .totalCourses(totalCourses)
                .totalEnrollments(totalEnrollments)
                .completedEnrollments(completedEnrollments)
                .inProgressEnrollments(inProgressEnrollments)
                .averageProgressPercentage(averageProgressPercentage)
                .topEnrolledCourses(topCourses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CertificationStatsResponse getCertificationStats() {
        long totalCertifications = certificationRepository.count();

        List<Object[]> orgData = certificationRepository.countCertificationsByOrganization();
        Map<String, Long> certsByOrg = new LinkedHashMap<>();
        for (Object[] row : orgData) {
            certsByOrg.put((String) row[0], ((Number) row[1]).longValue());
        }

        return CertificationStatsResponse.builder()
                .totalCertifications(totalCertifications)
                .certificationsByOrganization(certsByOrg)
                .build();
    }

    // --- Advanced Training Analytics Implementations ---

    @Override
    @Transactional(readOnly = true)
    public TrainingAnalyticsResponse getOverallTrainingAnalytics() {
        long totalCourses = courseRepository.count();
        List<Enrollment> allEnrollments = enrollmentRepository.findAll();
        long totalEnrollments = allEnrollments.size();

        long completed = allEnrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED).count();
        long inProgress = allEnrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.IN_PROGRESS).count();
        long notStarted = allEnrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED).count();

        double completionPct = totalEnrollments > 0 ? Math.round((double) completed / totalEnrollments * 100.0 * 10.0) / 10.0 : 0.0;
        double avgProgress = allEnrollments.stream()
                .mapToInt(e -> e.getProgressPercentage() != null ? e.getProgressPercentage() : 0)
                .average()
                .orElse(0.0);
        avgProgress = Math.round(avgProgress * 10.0) / 10.0;

        double totalTrainingHours = allEnrollments.stream()
                .mapToDouble(e -> {
                    int dur = e.getCourse().getDurationHours() != null ? e.getCourse().getDurationHours() : 0;
                    int prog = e.getProgressPercentage() != null ? e.getProgressPercentage() : 0;
                    return dur * (prog / 100.0);
                })
                .sum();
        totalTrainingHours = Math.round(totalTrainingHours * 10.0) / 10.0;

        long totalLearners = allEnrollments.stream()
                .map(e -> e.getUser().getId())
                .distinct()
                .count();

        long totalEmployees = userRepository.count();
        double enterpriseParticipationRate = totalEmployees > 0 ? Math.round((double) totalLearners / totalEmployees * 100.0 * 10.0) / 10.0 : 0.0;

        Map<String, Long> categoryBreakdown = allEnrollments.stream()
                .filter(e -> e.getCourse().getCategory() != null)
                .collect(Collectors.groupingBy(e -> e.getCourse().getCategory(), Collectors.counting()));

        List<DepartmentTrainingAnalyticsResponse> deptAnalytics = getAllDepartmentTrainingAnalytics();
        List<CourseTrainingAnalyticsResponse> topCourses = getAllCoursesTrainingAnalytics().stream()
                .sorted(Comparator.comparingLong(CourseTrainingAnalyticsResponse::getCompletedCount).reversed())
                .limit(5)
                .collect(Collectors.toList());

        List<TrainingTrendItem> trends = getTrainingTrends();

        return TrainingAnalyticsResponse.builder()
                .totalCourses(totalCourses)
                .totalEnrollments(totalEnrollments)
                .completedEnrollments(completed)
                .inProgressEnrollments(inProgress)
                .notStartedEnrollments(notStarted)
                .overallCompletionPercentage(completionPct)
                .averageTrainingProgressPercentage(avgProgress)
                .totalTrainingHoursCompleted(totalTrainingHours)
                .totalLearnersParticipating(totalLearners)
                .enterpriseTrainingParticipationRate(enterpriseParticipationRate)
                .enrollmentsByCategory(categoryBreakdown)
                .departmentAnalytics(deptAnalytics)
                .topPerformingCourses(topCourses)
                .monthlyTrends(trends)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeTrainingAnalyticsResponse getEmployeeTrainingAnalytics(Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new com.enterpriselearning.exception.ResourceNotFoundException("Employee not found with id: " + employeeId));

        List<Enrollment> enrollments = enrollmentRepository.findByUserId(employee.getId());
        long totalEnrolled = enrollments.size();
        long completed = enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED).count();
        long inProgress = enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.IN_PROGRESS).count();
        long notStarted = enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED).count();

        double completionPct = totalEnrolled > 0 ? Math.round((double) completed / totalEnrolled * 100.0 * 10.0) / 10.0 : 0.0;
        double avgProgress = enrollments.stream()
                .mapToInt(e -> e.getProgressPercentage() != null ? e.getProgressPercentage() : 0)
                .average()
                .orElse(0.0);
        avgProgress = Math.round(avgProgress * 10.0) / 10.0;

        double totalHours = enrollments.stream()
                .mapToDouble(e -> {
                    int dur = e.getCourse().getDurationHours() != null ? e.getCourse().getDurationHours() : 0;
                    int prog = e.getProgressPercentage() != null ? e.getProgressPercentage() : 0;
                    return dur * (prog / 100.0);
                })
                .sum();
        totalHours = Math.round(totalHours * 10.0) / 10.0;

        List<EmployeeCourseProgressDetail> details = enrollments.stream()
                .map(e -> {
                    int dur = e.getCourse().getDurationHours() != null ? e.getCourse().getDurationHours() : 0;
                    int prog = e.getProgressPercentage() != null ? e.getProgressPercentage() : 0;
                    double spent = Math.round(dur * (prog / 100.0) * 10.0) / 10.0;
                    return EmployeeCourseProgressDetail.builder()
                            .courseId(e.getCourse().getId())
                            .courseTitle(e.getCourse().getTitle())
                            .category(e.getCourse().getCategory())
                            .level(e.getCourse().getLevel())
                            .durationHours(dur)
                            .status(e.getStatus())
                            .progressPercentage(prog)
                            .hoursSpent(spent)
                            .enrolledDate(e.getEnrolledDate())
                            .completedDate(e.getCompletedDate())
                            .lastAccessedDate(e.getLastAccessedDate())
                            .build();
                })
                .collect(Collectors.toList());

        return EmployeeTrainingAnalyticsResponse.builder()
                .employeeId(employee.getId())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .employeeCode(employee.getEmployeeId())
                .employeeEmail(employee.getEmail())
                .department(employee.getDepartment())
                .totalEnrolledCourses(totalEnrolled)
                .completedCourses(completed)
                .inProgressCourses(inProgress)
                .notStartedCourses(notStarted)
                .completionPercentage(completionPct)
                .averageProgressPercentage(avgProgress)
                .totalTrainingHoursCompleted(totalHours)
                .courseProgressDetails(details)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeTrainingAnalyticsResponse getMyTrainingAnalytics(String userEmail) {
        User employee = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new com.enterpriselearning.exception.ResourceNotFoundException("User not found with email: " + userEmail));
        return getEmployeeTrainingAnalytics(employee.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentTrainingAnalyticsResponse getDepartmentTrainingAnalytics(String department) {
        List<User> deptEmployees = userRepository.findAll().stream()
                .filter(u -> u.getDepartment() != null && u.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());

        long totalEmployees = deptEmployees.size();
        Set<Long> employeeIds = deptEmployees.stream().map(User::getId).collect(Collectors.toSet());

        List<Enrollment> deptEnrollments = enrollmentRepository.findAll().stream()
                .filter(e -> employeeIds.contains(e.getUser().getId()))
                .collect(Collectors.toList());

        long totalEnrollments = deptEnrollments.size();
        long participating = deptEnrollments.stream().map(e -> e.getUser().getId()).distinct().count();
        double participationRate = totalEmployees > 0 ? Math.round((double) participating / totalEmployees * 100.0 * 10.0) / 10.0 : 0.0;

        long completed = deptEnrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED).count();
        long inProgress = deptEnrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.IN_PROGRESS).count();
        long notStarted = deptEnrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED).count();

        double completionPct = totalEnrollments > 0 ? Math.round((double) completed / totalEnrollments * 100.0 * 10.0) / 10.0 : 0.0;
        double avgProgress = deptEnrollments.stream()
                .mapToInt(e -> e.getProgressPercentage() != null ? e.getProgressPercentage() : 0)
                .average()
                .orElse(0.0);
        avgProgress = Math.round(avgProgress * 10.0) / 10.0;

        double totalHours = deptEnrollments.stream()
                .mapToDouble(e -> {
                    int dur = e.getCourse().getDurationHours() != null ? e.getCourse().getDurationHours() : 0;
                    int prog = e.getProgressPercentage() != null ? e.getProgressPercentage() : 0;
                    return dur * (prog / 100.0);
                })
                .sum();
        totalHours = Math.round(totalHours * 10.0) / 10.0;

        Map<String, Long> topCompleted = deptEnrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED)
                .collect(Collectors.groupingBy(e -> e.getCourse().getTitle(), Collectors.counting()));

        return DepartmentTrainingAnalyticsResponse.builder()
                .department(department)
                .totalDepartmentEmployees(totalEmployees)
                .participatingEmployees(participating)
                .participationRatePercentage(participationRate)
                .totalEnrollments(totalEnrollments)
                .completedEnrollments(completed)
                .inProgressEnrollments(inProgress)
                .notStartedEnrollments(notStarted)
                .completionPercentage(completionPct)
                .averageProgressPercentage(avgProgress)
                .totalTrainingHoursCompleted(totalHours)
                .topCompletedCourses(topCompleted)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentTrainingAnalyticsResponse> getAllDepartmentTrainingAnalytics() {
        Set<String> departments = userRepository.findAll().stream()
                .map(User::getDepartment)
                .filter(d -> d != null && !d.trim().isEmpty())
                .collect(Collectors.toSet());

        return departments.stream()
                .map(this::getDepartmentTrainingAnalytics)
                .sorted(Comparator.comparingDouble(DepartmentTrainingAnalyticsResponse::getTotalTrainingHoursCompleted).reversed())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CourseTrainingAnalyticsResponse getCourseTrainingAnalytics(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new com.enterpriselearning.exception.ResourceNotFoundException("Course not found with id: " + courseId));

        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        long total = enrollments.size();
        long completed = enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED).count();
        long inProgress = enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.IN_PROGRESS).count();
        long notStarted = enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED).count();

        double completionPct = total > 0 ? Math.round((double) completed / total * 100.0 * 10.0) / 10.0 : 0.0;
        double avgProgress = enrollments.stream()
                .mapToInt(e -> e.getProgressPercentage() != null ? e.getProgressPercentage() : 0)
                .average()
                .orElse(0.0);
        avgProgress = Math.round(avgProgress * 10.0) / 10.0;

        int dur = course.getDurationHours() != null ? course.getDurationHours() : 0;
        double totalHours = enrollments.stream()
                .mapToDouble(e -> {
                    int prog = e.getProgressPercentage() != null ? e.getProgressPercentage() : 0;
                    return dur * (prog / 100.0);
                })
                .sum();
        totalHours = Math.round(totalHours * 10.0) / 10.0;

        return CourseTrainingAnalyticsResponse.builder()
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .category(course.getCategory())
                .level(course.getLevel())
                .durationHours(dur)
                .instructor(course.getInstructor())
                .totalEnrollments(total)
                .completedCount(completed)
                .inProgressCount(inProgress)
                .notStartedCount(notStarted)
                .completionPercentage(completionPct)
                .averageProgressPercentage(avgProgress)
                .totalTrainingHoursDelivered(totalHours)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseTrainingAnalyticsResponse> getAllCoursesTrainingAnalytics() {
        return courseRepository.findAll().stream()
                .map(c -> getCourseTrainingAnalytics(c.getId()))
                .sorted(Comparator.comparingLong(CourseTrainingAnalyticsResponse::getTotalEnrollments).reversed())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingTrendItem> getTrainingTrends() {
        List<Enrollment> enrollments = enrollmentRepository.findAll();
        Map<String, List<Enrollment>> byPeriod = enrollments.stream()
                .collect(Collectors.groupingBy(e -> {
                    if (e.getEnrolledDate() != null) {
                        return e.getEnrolledDate().getYear() + "-" + String.format("%02d", e.getEnrolledDate().getMonthValue());
                    }
                    return "Recent";
                }));

        List<TrainingTrendItem> items = new ArrayList<>();
        for (Map.Entry<String, List<Enrollment>> entry : byPeriod.entrySet()) {
            String period = entry.getKey();
            List<Enrollment> list = entry.getValue();
            long newEnr = list.size();
            long comp = list.stream().filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED).count();
            double hours = list.stream()
                    .mapToDouble(e -> {
                        int dur = e.getCourse().getDurationHours() != null ? e.getCourse().getDurationHours() : 0;
                        int prog = e.getProgressPercentage() != null ? e.getProgressPercentage() : 0;
                        return dur * (prog / 100.0);
                    })
                    .sum();
            items.add(TrainingTrendItem.builder()
                    .period(period)
                    .newEnrollments(newEnr)
                    .completedCourses(comp)
                    .trainingHoursCompleted(Math.round(hours * 10.0) / 10.0)
                    .build());
        }

        items.sort(Comparator.comparing(TrainingTrendItem::getPeriod));
        return items;
    }
}
