package com.enterpriselearning;

import com.enterpriselearning.dto.request.*;
import com.enterpriselearning.dto.response.*;
import com.enterpriselearning.entity.*;
import com.enterpriselearning.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EnterpriseLearningFlowTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private SkillService skillService;

    @Autowired
    private LearningService learningService;

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private CareerService careerService;

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private PromotionCriteriaService promotionCriteriaService;

    @Autowired
    private JobPortalService jobPortalService;

    @Autowired
    private JobMatchingService jobMatchingService;

    @Test
    void testAuthAndUserFlow() {
        // 1. Register new user
        RegisterRequest register = RegisterRequest.builder()
                .firstName("Test")
                .lastName("User")
                .email("test.user@company.com")
                .password("testpass123")
                .phone("1234567890")
                .department("Engineering")
                .role(Role.EMPLOYEE)
                .build();

        AuthResponse regResponse = authService.register(register);
        assertNotNull(regResponse.getToken());
        assertEquals("test.user@company.com", regResponse.getEmail());

        // 2. Login
        LoginRequest login = LoginRequest.builder()
                .email("test.user@company.com")
                .password("testpass123")
                .build();

        AuthResponse loginResponse = authService.login(login);
        assertNotNull(loginResponse.getToken());
        assertEquals("EMPLOYEE", loginResponse.getRole());
    }

    @Test
    void testEmployeeManagement() {
        List<UserResponse> employees = employeeService.getAllEmployees();
        assertFalse(employees.isEmpty());

        UserResponse firstEmp = employees.get(0);
        UserResponse fetched = employeeService.getEmployeeById(firstEmp.getId());
        assertEquals(firstEmp.getEmail(), fetched.getEmail());
    }

    @Test
    void testSkillManagementAndAssignment() {
        SkillRequest skillReq = SkillRequest.builder()
                .name("GraphQL API Design")
                .category("Backend Development")
                .description("Designing flexible schema APIs")
                .build();

        SkillResponse createdSkill = skillService.createSkill(skillReq);
        assertNotNull(createdSkill.getId());

        UserResponse emp = employeeService.getAllEmployees().get(0);

        SkillAssignRequest assignReq = SkillAssignRequest.builder()
                .userId(emp.getId())
                .skillId(createdSkill.getId())
                .proficiencyLevel(ProficiencyLevel.ADVANCED)
                .verified(true)
                .yearsOfExperience(3)
                .build();

        EmployeeSkillResponse assigned = skillService.assignSkillToEmployee(assignReq);
        assertEquals(ProficiencyLevel.ADVANCED, assigned.getProficiencyLevel());

        List<EmployeeSkillResponse> empSkills = skillService.getSkillsByEmployee(emp.getId());
        assertTrue(empSkills.stream().anyMatch(s -> s.getSkillName().equals("GraphQL API Design")));
    }

    @Test
    void testLearningAndEnrollment() {
        CourseRequest courseReq = CourseRequest.builder()
                .title("Microservices Architecture with Spring Cloud")
                .description("Advanced Spring Cloud microservices patterns")
                .category("Backend Development")
                .level("Advanced")
                .durationHours(35)
                .instructor("Prof. Cloud")
                .build();

        CourseResponse createdCourse = learningService.createCourse(courseReq);
        assertNotNull(createdCourse.getId());

        UserResponse emp = employeeService.getAllEmployees().get(0);

        EnrollmentRequest enrollReq = EnrollmentRequest.builder()
                .userId(emp.getId())
                .courseId(createdCourse.getId())
                .build();

        EnrollmentResponse enrollment = learningService.enrollInCourse(enrollReq, emp.getEmail());
        assertEquals(0, enrollment.getProgressPercentage());

        ProgressUpdateRequest progressReq = ProgressUpdateRequest.builder()
                .progressPercentage(100)
                .build();

        EnrollmentResponse updatedEnrollment = learningService.updateProgress(enrollment.getId(), progressReq, emp.getEmail());
        assertEquals(100, updatedEnrollment.getProgressPercentage());
    }

    @Test
    void testCertificationManagement() {
        UserResponse emp = employeeService.getAllEmployees().get(0);

        CertificationRequest certReq = CertificationRequest.builder()
                .userId(emp.getId())
                .title("AWS Certified Solutions Architect - Associate")
                .issuingOrganization("Amazon Web Services")
                .issueDate(LocalDate.now().minusDays(30))
                .expiryDate(LocalDate.now().plusYears(3))
                .credentialId("AWS-SAA-998877")
                .credentialUrl("https://aws.amazon.com/verification")
                .build();

        CertificationResponse cert = certificationService.addCertification(certReq, emp.getEmail());
        assertNotNull(cert.getId());
        assertEquals("Amazon Web Services", cert.getIssuingOrganization());
    }

    @Test
    void testCareerGuidanceAndRecommendations() {
        UserResponse emp = employeeService.getAllEmployees().get(0);

        CareerGoalRequest goalReq = CareerGoalRequest.builder()
                .userId(emp.getId())
                .targetRole("Lead Software Engineer")
                .targetDate(LocalDate.now().plusMonths(12))
                .status("IN_PROGRESS")
                .notes("Focusing on cloud architecture and mentoring")
                .build();

        CareerGoalResponse goal = careerService.createCareerGoal(goalReq, emp.getEmail());
        assertNotNull(goal.getId());

        SkillRecommendationResponse recommendations = careerService.getRecommendationsForEmployee(emp.getId(), "Senior Backend Engineer");
        assertNotNull(recommendations);
        assertNotNull(recommendations.getRecommendedSkills());
    }

    @Test
    void testAnalyticsDashboard() {
        AnalyticsDashboardResponse dashboard = analyticsService.getDashboardAnalytics();
        assertNotNull(dashboard);
        assertNotNull(dashboard.getEmployeeStats());
        assertNotNull(dashboard.getSkillStats());
        assertNotNull(dashboard.getLearningStats());
        assertNotNull(dashboard.getCertificationStats());
        assertTrue(dashboard.getEmployeeStats().getTotalEmployees() >= 1);
    }

    @Test
    void testSettingsAndProfileUpdate() {
        UserResponse emp = employeeService.getAllEmployees().get(0);

        ProfileUpdateRequest updateReq = ProfileUpdateRequest.builder()
                .firstName(emp.getFirstName())
                .lastName(emp.getLastName())
                .email(emp.getEmail())
                .phone("+1-999-888-7777")
                .department("Core Platform")
                .build();

        UserResponse updated = settingsService.updateProfile(updateReq, emp.getEmail());
        assertEquals("+1-999-888-7777", updated.getPhone());
        assertEquals("Core Platform", updated.getDepartment());
    }

    @Test
    void testPromotionCriteriaAndEvaluation() {
        PromotionCriteriaRequest request = PromotionCriteriaRequest.builder()
                .name("Senior Engineer Level 2")
                .description("Criteria for Senior Engineer evaluation")
                .minimumScore(70)
                .targetRole("Senior Backend Engineer")
                .requiredDepartment("Engineering")
                .minExperienceYears(3)
                .requiredSkills("Java, Spring Boot")
                .minCompletedCourses(1)
                .minCertifications(1)
                .active(true)
                .build();

        PromotionCriteriaResponse created = promotionCriteriaService.createCriteria(request);
        assertNotNull(created.getId());
        assertEquals("Senior Engineer Level 2", created.getName());

        UserResponse emp = employeeService.getAllEmployees().get(0);
        PromotionEvaluationResponse evaluation = promotionCriteriaService.evaluateEmployee(emp.getId(), created.getId());
        assertNotNull(evaluation);
        assertNotNull(evaluation.getFeedback());
        assertTrue(evaluation.getOverallScore() > 0);
    }

    @Test
    void testInternalJobPortalFlow() {
        // 1. Create a Job Posting
        JobPostingRequest jobReq = JobPostingRequest.builder()
                .title("Senior Cloud Backend Engineer")
                .department("Engineering")
                .location("New York / Remote")
                .employmentType("Full-time")
                .description("Build high-performance microservices and cloud systems.")
                .requiredSkills("Java, Spring Boot, SQL & Database Design, Docker & Containers")
                .minExperienceYears(3)
                .deadline(LocalDate.now().plusMonths(2))
                .status(JobPostingStatus.OPEN)
                .build();

        JobPostingResponse job = jobPortalService.createJobPosting(jobReq, "manager@enterpriselearning.com");
        assertNotNull(job.getId());
        assertEquals("Senior Cloud Backend Engineer", job.getTitle());

        // 2. Employee applies for job
        UserResponse emp = employeeService.getAllEmployees().stream()
                .filter(e -> e.getEmail().equals("employee@enterpriselearning.com"))
                .findFirst()
                .orElse(employeeService.getAllEmployees().get(0));
        JobApplicationRequest appReq = JobApplicationRequest.builder()
                .jobPostingId(job.getId())
                .coverNote("Very interested in cloud backend engineering.")
                .build();

        JobApplicationResponse app = jobPortalService.applyForJob(appReq, emp.getEmail());
        assertNotNull(app.getId());
        assertEquals(JobApplicationStatus.APPLIED, app.getStatus());
        assertTrue(app.getMatchScore() > 0);

        // 3. Manager reviews and shortlists application
        ApplicationStatusUpdateRequest statusReq = ApplicationStatusUpdateRequest.builder()
                .status(JobApplicationStatus.SHORTLISTED)
                .build();

        JobApplicationResponse updatedApp = jobPortalService.updateApplicationStatus(app.getId(), statusReq);
        assertEquals(JobApplicationStatus.SHORTLISTED, updatedApp.getStatus());

        // 4. Retrieve applications for job
        List<JobApplicationResponse> jobApps = jobPortalService.getApplicationsByJob(job.getId());
        assertFalse(jobApps.isEmpty());

        // 5. Retrieve my applications
        List<JobApplicationResponse> myApps = jobPortalService.getMyApplications(emp.getEmail());
        assertFalse(myApps.isEmpty());
    }

    @Test
    void testJobMatchingEngineFlow() {
        UserResponse emp = employeeService.getAllEmployees().stream()
                .filter(e -> e.getEmail().equals("employee@enterpriselearning.com"))
                .findFirst()
                .orElse(employeeService.getAllEmployees().get(0));

        List<JobPostingResponse> jobs = jobPortalService.getAllJobPostings(JobPostingStatus.OPEN, null, null);
        assertFalse(jobs.isEmpty());

        Long jobId = jobs.get(0).getId();

        // 1. Single Job Match Analysis
        JobMatchResponse match = jobMatchingService.matchEmployeeWithJob(emp.getId(), jobId);
        assertNotNull(match);
        assertNotNull(match.getCompatibilityLevel());
        assertTrue(match.getOverallMatchScore() >= 0.0);
        assertNotNull(match.getSkillDetails());
        assertNotNull(match.getGapAnalysis());

        // 2. Ranked matched jobs for employee
        List<JobMatchResponse> matchedJobs = jobMatchingService.findMatchedJobsForEmployee(emp.getId(), 0.0);
        assertFalse(matchedJobs.isEmpty());
        assertTrue(matchedJobs.get(0).getOverallMatchScore() >= 0.0);

        // 3. Ranked candidates for job
        List<CandidateMatchResponse> candidates = jobMatchingService.findMatchedCandidatesForJob(jobId, 0.0);
        assertFalse(candidates.isEmpty());
        assertNotNull(candidates.get(0).getCandidateName());
    }

    @Test
    void testCareerProgressPercentageCalculation() {
        UserResponse emp = employeeService.getAllEmployees().stream()
                .filter(e -> e.getEmail().equals("employee@enterpriselearning.com"))
                .findFirst()
                .orElse(employeeService.getAllEmployees().get(0));

        // 1. Calculate My Career Progress
        CareerProgressResponse progress = careerService.calculateMyCareerProgress(emp.getEmail(), null);
        assertNotNull(progress);
        assertNotNull(progress.getTargetRole());
        assertNotNull(progress.getReadinessStage());
        assertTrue(progress.getOverallProgressPercentage() > 0.0);
        assertTrue(progress.getSkillProgressPercentage() > 0.0);
        assertTrue(progress.getCourseProgressPercentage() >= 0.0);
        assertTrue(progress.getCertificationProgressPercentage() > 0.0);
        assertTrue(progress.getExperienceProgressPercentage() > 0.0);
        assertNotNull(progress.getActionableMilestones());
        assertFalse(progress.getActionableMilestones().isEmpty());

        // 2. Calculate Progress across all configured paths
        List<CareerProgressResponse> allProgress = careerService.getMyCareerProgressForAllGoals(emp.getEmail());
        assertFalse(allProgress.isEmpty());
        assertTrue(allProgress.get(0).getOverallProgressPercentage() >= 0.0);
    }

    @Test
    void testAdvancedTrainingAnalyticsFlow() {
        // 1. Overall Training Analytics
        TrainingAnalyticsResponse overall = analyticsService.getOverallTrainingAnalytics();
        assertNotNull(overall);
        assertTrue(overall.getTotalCourses() > 0);
        assertTrue(overall.getTotalEnrollments() > 0);
        assertTrue(overall.getCompletedEnrollments() >= 0);
        assertTrue(overall.getInProgressEnrollments() >= 0);
        assertTrue(overall.getTotalTrainingHoursCompleted() >= 0.0);
        assertNotNull(overall.getDepartmentAnalytics());
        assertNotNull(overall.getTopPerformingCourses());
        assertNotNull(overall.getMonthlyTrends());

        // 2. Employee Training Analytics
        UserResponse emp = employeeService.getAllEmployees().stream()
                .filter(e -> e.getEmail().equals("employee@enterpriselearning.com"))
                .findFirst()
                .orElse(employeeService.getAllEmployees().get(0));

        EmployeeTrainingAnalyticsResponse empAnalytics = analyticsService.getEmployeeTrainingAnalytics(emp.getId());
        assertNotNull(empAnalytics);
        assertEquals(emp.getId(), empAnalytics.getEmployeeId());
        assertTrue(empAnalytics.getTotalEnrolledCourses() > 0);
        assertTrue(empAnalytics.getTotalTrainingHoursCompleted() >= 0.0);
        assertNotNull(empAnalytics.getCourseProgressDetails());
        assertFalse(empAnalytics.getCourseProgressDetails().isEmpty());

        // 3. Department Training Analytics
        DepartmentTrainingAnalyticsResponse deptAnalytics = analyticsService.getDepartmentTrainingAnalytics("Engineering");
        assertNotNull(deptAnalytics);
        assertEquals("Engineering", deptAnalytics.getDepartment());
        assertTrue(deptAnalytics.getTotalDepartmentEmployees() > 0);
        assertTrue(deptAnalytics.getParticipatingEmployees() > 0);

        // 4. Course Training Analytics
        CourseTrainingAnalyticsResponse courseAnalytics = analyticsService.getCourseTrainingAnalytics(1L);
        assertNotNull(courseAnalytics);
        assertNotNull(courseAnalytics.getCourseTitle());
        assertTrue(courseAnalytics.getTotalEnrollments() >= 0);

        // 5. Training Trends
        List<TrainingTrendItem> trends = analyticsService.getTrainingTrends();
        assertNotNull(trends);
        assertFalse(trends.isEmpty());
        assertNotNull(trends.get(0).getPeriod());
    }
}
