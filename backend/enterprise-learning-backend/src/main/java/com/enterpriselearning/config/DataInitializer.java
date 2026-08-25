package com.enterpriselearning.config;

import com.enterpriselearning.entity.*;
import com.enterpriselearning.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final EmployeeSkillRepository employeeSkillRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CertificationRepository certificationRepository;
    private final CareerPathRepository careerPathRepository;
    private final CareerGoalRepository careerGoalRepository;
    private final PromotionCriteriaRepository promotionCriteriaRepository;
    private final JobPostingRepository jobPostingRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Initializing enterprise learning platform demo & seed data...");

            // 1. Create Default Users (Admin, Manager, Employee)
            User admin = userRepository.save(User.builder()
                    .firstName("Admin")
                    .lastName("System")
                    .email("admin@enterpriselearning.com")
                    .password(passwordEncoder.encode("admin123"))
                    .phone("+1-555-0100")
                    .employeeId("EMP-ADM-001")
                    .department("Information Technology")
                    .role(Role.ADMIN)
                    .build());

            User manager = userRepository.save(User.builder()
                    .firstName("Sarah")
                    .lastName("Connor")
                    .email("manager@enterpriselearning.com")
                    .password(passwordEncoder.encode("manager123"))
                    .phone("+1-555-0101")
                    .employeeId("EMP-MGR-002")
                    .department("Engineering")
                    .role(Role.HR)
                    .build());

            User employee1 = userRepository.save(User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("employee@enterpriselearning.com")
                    .password(passwordEncoder.encode("employee123"))
                    .phone("+1-555-0102")
                    .employeeId("EMP-DEV-003")
                    .department("Engineering")
                    .role(Role.EMPLOYEE)
                    .build());

            User employee2 = userRepository.save(User.builder()
                    .firstName("Alice")
                    .lastName("Smith")
                    .email("alice@enterpriselearning.com")
                    .password(passwordEncoder.encode("employee123"))
                    .phone("+1-555-0103")
                    .employeeId("EMP-DEV-004")
                    .department("Product")
                    .role(Role.EMPLOYEE)
                    .build());

            // 2. Create Skills
            Skill javaSkill = skillRepository.save(Skill.builder()
                    .name("Java")
                    .description("Core Java, Object-Oriented Programming, Multithreading, Streams API")
                    .category("Backend Development")
                    .build());

            Skill springSkill = skillRepository.save(Skill.builder()
                    .name("Spring Boot")
                    .description("REST APIs, Spring Data JPA, Spring Security, Microservices")
                    .category("Backend Development")
                    .build());

            Skill dockerSkill = skillRepository.save(Skill.builder()
                    .name("Docker & Containers")
                    .description("Containerization, Dockerfile, Container Orchestration")
                    .category("DevOps & Cloud")
                    .build());

            Skill k8sSkill = skillRepository.save(Skill.builder()
                    .name("Kubernetes")
                    .description("Cluster management, deployments, ingress, Helm")
                    .category("DevOps & Cloud")
                    .build());

            Skill reactSkill = skillRepository.save(Skill.builder()
                    .name("React.js")
                    .description("Component architecture, state management, hooks, TypeScript")
                    .category("Frontend Development")
                    .build());

            Skill sqlSkill = skillRepository.save(Skill.builder()
                    .name("SQL & Database Design")
                    .description("Relational databases, indexing, query optimization, MySQL, PostgreSQL")
                    .category("Database")
                    .build());

            Skill systemDesignSkill = skillRepository.save(Skill.builder()
                    .name("System Design & Architecture")
                    .description("High-level architecture, scalability, caching, load balancing, message queues")
                    .category("Architecture")
                    .build());

            // 3. Assign Skills to Employee
            employeeSkillRepository.save(EmployeeSkill.builder()
                    .user(employee1)
                    .skill(javaSkill)
                    .proficiencyLevel(ProficiencyLevel.ADVANCED)
                    .verified(true)
                    .yearsOfExperience(4)
                    .build());

            employeeSkillRepository.save(EmployeeSkill.builder()
                    .user(employee1)
                    .skill(springSkill)
                    .proficiencyLevel(ProficiencyLevel.INTERMEDIATE)
                    .verified(true)
                    .yearsOfExperience(3)
                    .build());

            employeeSkillRepository.save(EmployeeSkill.builder()
                    .user(employee1)
                    .skill(sqlSkill)
                    .proficiencyLevel(ProficiencyLevel.ADVANCED)
                    .verified(true)
                    .yearsOfExperience(4)
                    .build());

            employeeSkillRepository.save(EmployeeSkill.builder()
                    .user(employee2)
                    .skill(reactSkill)
                    .proficiencyLevel(ProficiencyLevel.INTERMEDIATE)
                    .verified(true)
                    .yearsOfExperience(2)
                    .build());

            // 4. Create Courses
            Course course1 = courseRepository.save(Course.builder()
                    .title("Mastering Spring Boot 3 & Cloud Native Java")
                    .description("Comprehensive guide to building production-ready scalable microservices with Spring Boot 3, Spring Cloud, and Docker.")
                    .category("Backend Development")
                    .level("Intermediate")
                    .durationHours(30)
                    .instructor("Dr. Alex Bennett")
                    .build());

            Course course2 = courseRepository.save(Course.builder()
                    .title("Kubernetes for Enterprise Developers")
                    .description("Learn cluster orchestration, microservices deployment, CI/CD pipelines, and zero-downtime rolling updates.")
                    .category("DevOps & Cloud")
                    .level("Advanced")
                    .durationHours(25)
                    .instructor("Marcus Vance")
                    .build());

            Course course3 = courseRepository.save(Course.builder()
                    .title("Enterprise System Architecture & Scalability")
                    .description("Deep dive into distributed systems, CAP theorem, eventual consistency, Kafka event streaming, and cache strategies.")
                    .category("Architecture")
                    .level("Advanced")
                    .durationHours(40)
                    .instructor("Elena Rostova")
                    .build());

            Course course4 = courseRepository.save(Course.builder()
                    .title("Relational Database Optimization & MySQL Tuning")
                    .description("Master execution plans, indexing strategies, locking mechanisms, and high-throughput query design.")
                    .category("Database")
                    .level("Intermediate")
                    .durationHours(20)
                    .instructor("David Zhang")
                    .build());

            // 5. Enrollments
            enrollmentRepository.save(Enrollment.builder()
                    .user(employee1)
                    .course(course1)
                    .status(EnrollmentStatus.IN_PROGRESS)
                    .progressPercentage(65)
                    .build());

            enrollmentRepository.save(Enrollment.builder()
                    .user(employee1)
                    .course(course4)
                    .status(EnrollmentStatus.COMPLETED)
                    .progressPercentage(100)
                    .build());

            enrollmentRepository.save(Enrollment.builder()
                    .user(employee2)
                    .course(course1)
                    .status(EnrollmentStatus.ENROLLED)
                    .progressPercentage(15)
                    .build());

            // 6. Certifications
            certificationRepository.save(Certification.builder()
                    .user(employee1)
                    .title("Oracle Certified Professional: Java SE 17 Developer")
                    .issuingOrganization("Oracle")
                    .issueDate(LocalDate.now().minusMonths(14))
                    .expiryDate(LocalDate.now().plusMonths(22))
                    .credentialId("OCP-JAVA-884920")
                    .credentialUrl("https://verify.oracle.com/cert/884920")
                    .build());

            certificationRepository.save(Certification.builder()
                    .user(employee1)
                    .title("Spring Certified Professional 2024")
                    .issuingOrganization("VMware Tanzu")
                    .issueDate(LocalDate.now().minusMonths(6))
                    .expiryDate(LocalDate.now().plusMonths(30))
                    .credentialId("VMW-SCP-551029")
                    .credentialUrl("https://www.credly.com/org/vmware")
                    .build());

            // 7. Career Paths
            careerPathRepository.save(CareerPath.builder()
                    .title("Senior Backend Software Engineer")
                    .targetRole("Senior Backend Engineer")
                    .department("Engineering")
                    .description("Responsible for designing scalable microservices, high-throughput APIs, and guiding junior engineers.")
                    .requiredSkills("Java, Spring Boot, SQL & Database Design, Docker & Containers, System Design & Architecture")
                    .recommendedCourses("Mastering Spring Boot 3 & Cloud Native Java, Enterprise System Architecture & Scalability")
                    .minExperienceYears(4)
                    .build());

            careerPathRepository.save(CareerPath.builder()
                    .title("Principal Cloud & Enterprise Architect")
                    .targetRole("Enterprise Architect")
                    .department("Architecture")
                    .description("Defines enterprise technology strategy, cloud adoption roadmap, and distributed governance standards.")
                    .requiredSkills("System Design & Architecture, Kubernetes, Docker & Containers, Java, Spring Boot")
                    .recommendedCourses("Enterprise System Architecture & Scalability, Kubernetes for Enterprise Developers")
                    .minExperienceYears(7)
                    .build());

            // 8. Career Goal
            careerGoalRepository.save(CareerGoal.builder()
                    .user(employee1)
                    .targetRole("Senior Backend Engineer")
                    .targetDate(LocalDate.now().plusMonths(6))
                    .status("IN_PROGRESS")
                    .notes("Completing Docker & System Design milestones and leading sprint architectures.")
                    .build());

            // 9. Promotion Criteria
            promotionCriteriaRepository.save(PromotionCriteria.builder()
                    .name("Senior Engineer Promotion Benchmark")
                    .description("Criteria for promotion to Senior Backend Software Engineer")
                    .minimumScore(75)
                    .targetRole("Senior Backend Engineer")
                    .requiredDepartment("Engineering")
                    .minExperienceYears(3)
                    .requiredSkills("Java, Spring Boot, SQL & Database Design")
                    .minCompletedCourses(1)
                    .minCertifications(1)
                    .active(true)
                    .build());

            promotionCriteriaRepository.save(PromotionCriteria.builder()
                    .name("Tech Lead / Architect Promotion Criteria")
                    .description("Requirements for transition into Technical Leadership & Architecture")
                    .minimumScore(85)
                    .targetRole("Enterprise Architect")
                    .requiredDepartment("Engineering")
                    .minExperienceYears(6)
                    .requiredSkills("System Design & Architecture, Kubernetes, Docker & Containers, Java")
                    .minCompletedCourses(3)
                    .minCertifications(2)
                    .active(true)
                    .build());

            // 10. Job Postings
            JobPosting job1 = jobPostingRepository.save(JobPosting.builder()
                    .title("Senior Cloud Backend Engineer")
                    .department("Engineering")
                    .location("New York, NY / Hybrid")
                    .employmentType("Full-time")
                    .description("Lead the design and development of enterprise distributed services and streaming pipelines.")
                    .requiredSkills("Java, Spring Boot, SQL & Database Design, Docker & Containers")
                    .minExperienceYears(3)
                    .status(JobPostingStatus.OPEN)
                    .postedBy(manager)
                    .deadline(LocalDate.now().plusMonths(2))
                    .build());

            jobPostingRepository.save(JobPosting.builder()
                    .title("Staff Solutions Architect")
                    .department("Architecture")
                    .location("Remote")
                    .employmentType("Full-time")
                    .description("Provide architecture leadership across engineering pods and establish cloud excellence standards.")
                    .requiredSkills("System Design & Architecture, Kubernetes, Java, Docker & Containers")
                    .minExperienceYears(6)
                    .status(JobPostingStatus.OPEN)
                    .postedBy(admin)
                    .deadline(LocalDate.now().plusMonths(3))
                    .build());

            // 11. Job Application
            jobApplicationRepository.save(JobApplication.builder()
                    .jobPosting(job1)
                    .applicant(employee1)
                    .status(JobApplicationStatus.APPLIED)
                    .matchScore(75.0)
                    .coverNote("Excited to step up to the Senior Cloud Backend Engineer role and contribute to the microservices migration.")
                    .build());

            log.info("Enterprise Learning Platform seed data initialized successfully!");
        }
    }
}
