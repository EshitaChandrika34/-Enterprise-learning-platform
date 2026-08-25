package com.enterpriselearning.service.impl;

import com.enterpriselearning.dto.response.*;
import com.enterpriselearning.entity.*;
import com.enterpriselearning.exception.ResourceNotFoundException;
import com.enterpriselearning.repository.*;
import com.enterpriselearning.service.JobMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobMatchingServiceImpl implements JobMatchingService {

    private final JobPostingRepository jobPostingRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final EmployeeSkillRepository employeeSkillRepository;
    private final CourseRepository courseRepository;
    private final CertificationRepository certificationRepository;


    // ==========================================
    // MATCH EMPLOYEE WITH JOB
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public JobMatchResponse matchEmployeeWithJob(
            Long employeeId,
            Long jobId) {

        User employee =
                userRepository
                        .findById(employeeId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Employee not found with id: "
                                                        + employeeId
                                        )
                        );


        JobPosting job =
                jobPostingRepository
                        .findById(jobId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Job posting not found with id: "
                                                        + jobId
                                        )
                        );


        return computeJobMatch(
                employee,
                job
        );
    }


    // ==========================================
    // MATCH LOGGED IN USER WITH JOB
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public JobMatchResponse matchMyProfileWithJob(
            String userEmail,
            Long jobId) {

        User employee =
                userRepository
                        .findByEmail(userEmail)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "User not found with email: "
                                                        + userEmail
                                        )
                        );


        JobPosting job =
                jobPostingRepository
                        .findById(jobId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Job posting not found with id: "
                                                        + jobId
                                        )
                        );


        return computeJobMatch(
                employee,
                job
        );
    }


    // ==========================================
    // FIND MATCHED JOBS FOR EMPLOYEE
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public List<JobMatchResponse>
    findMatchedJobsForEmployee(
            Long employeeId,
            Double minScore) {

        User employee =
                userRepository
                        .findById(employeeId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Employee not found with id: "
                                                        + employeeId
                                        )
                        );


        List<JobPosting> openJobs =
                jobPostingRepository
                        .findByStatus(
                                JobPostingStatus.OPEN
                        );


        double threshold =
                minScore != null
                        ? minScore
                        : 0.0;


        return openJobs
                .stream()
                .map(
                        job ->
                                computeJobMatch(
                                        employee,
                                        job
                                )
                )
                .filter(
                        match ->
                                match.getOverallMatchScore()
                                        >=
                                        threshold
                )
                .sorted(
                        Comparator
                                .comparingDouble(
                                        JobMatchResponse
                                                ::getOverallMatchScore
                                )
                                .reversed()
                )
                .collect(
                        Collectors.toList()
                );
    }


    // ==========================================
    // FIND MATCHED JOBS FOR LOGGED USER
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public List<JobMatchResponse>
    findMatchedJobsForMe(
            String userEmail,
            Double minScore) {

        User employee =
                userRepository
                        .findByEmail(userEmail)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "User not found with email: "
                                                        + userEmail
                                        )
                        );


        List<JobPosting> openJobs =
                jobPostingRepository
                        .findByStatus(
                                JobPostingStatus.OPEN
                        );


        double threshold =
                minScore != null
                        ? minScore
                        : 0.0;


        return openJobs
                .stream()
                .map(
                        job ->
                                computeJobMatch(
                                        employee,
                                        job
                                )
                )
                .filter(
                        match ->
                                match.getOverallMatchScore()
                                        >=
                                        threshold
                )
                .sorted(
                        Comparator
                                .comparingDouble(
                                        JobMatchResponse
                                                ::getOverallMatchScore
                                )
                                .reversed()
                )
                .collect(
                        Collectors.toList()
                );
    }


    // ==========================================
    // FIND MATCHED CANDIDATES FOR JOB
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public List<CandidateMatchResponse>
    findMatchedCandidatesForJob(
            Long jobId,
            Double minScore) {

        JobPosting job =
                jobPostingRepository
                        .findById(jobId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Job posting not found with id: "
                                                        + jobId
                                        )
                        );


        /*
         * IMPORTANT:
         *
         * Old code used Role.MANAGER.
         * We changed the final platform roles to:
         *
         * ADMIN
         * HR
         * EMPLOYEE
         *
         * Therefore MANAGER is replaced with HR.
         */

        List<User> employees =
                userRepository
                        .findAll()
                        .stream()
                        .filter(
                                user ->
                                        user.getRole()
                                                ==
                                                Role.EMPLOYEE
                                        ||
                                        user.getRole()
                                                ==
                                                Role.HR
                        )
                        .collect(
                                Collectors.toList()
                        );


        double threshold =
                minScore != null
                        ? minScore
                        : 0.0;


        return employees
                .stream()
                .map(
                        employee ->
                                computeCandidateMatch(
                                        employee,
                                        job
                                )
                )
                .filter(
                        match ->
                                match.getOverallMatchScore()
                                        >=
                                        threshold
                )
                .sorted(
                        Comparator
                                .comparingDouble(
                                        CandidateMatchResponse
                                                ::getOverallMatchScore
                                )
                                .reversed()
                )
                .collect(
                        Collectors.toList()
                );
    }


    // ==========================================
    // COMPUTE JOB MATCH
    // ==========================================

    private JobMatchResponse computeJobMatch(
            User employee,
            JobPosting job) {

        List<String> reqSkills =
                parseCommaList(
                        job.getRequiredSkills()
                );


        List<EmployeeSkill> empSkills =
                employeeSkillRepository
                        .findByUserId(
                                employee.getId()
                        );


        Map<String, EmployeeSkill>
                empSkillMap =
                new HashMap<>();


        for (
                EmployeeSkill employeeSkill
                :
                empSkills
        ) {

            empSkillMap.put(
                    employeeSkill
                            .getSkill()
                            .getName()
                            .trim()
                            .toLowerCase(),

                    employeeSkill
            );
        }


        // ==========================================
        // 1. SKILL MATCH
        // MAXIMUM 60 POINTS
        // ==========================================

        List<SkillMatchDetail>
                skillDetails =
                new ArrayList<>();


        List<String>
                matchedSkills =
                new ArrayList<>();


        List<String>
                missingSkills =
                new ArrayList<>();


        double earnedSkillPoints =
                0.0;


        for (
                String reqSkill
                :
                reqSkills
        ) {

            String lower =
                    reqSkill
                            .toLowerCase();


            if (
                    empSkillMap
                            .containsKey(
                                    lower
                            )
            ) {

                EmployeeSkill employeeSkill =
                        empSkillMap
                                .get(
                                        lower
                                );


                double weight =
                        getProficiencyWeight(
                                employeeSkill
                                        .getProficiencyLevel()
                        );


                earnedSkillPoints +=
                        weight;


                matchedSkills.add(
                        reqSkill
                );


                skillDetails.add(

                        SkillMatchDetail
                                .builder()

                                .skillName(
                                        reqSkill
                                )

                                .matched(
                                        true
                                )

                                .proficiencyLevel(
                                        employeeSkill
                                                .getProficiencyLevel()
                                )

                                .yearsOfExperience(
                                        employeeSkill
                                                .getYearsOfExperience()
                                )

                                .earnedWeight(
                                        weight
                                )

                                .build()
                );

            } else {

                missingSkills.add(
                        reqSkill
                );


                skillDetails.add(

                        SkillMatchDetail
                                .builder()

                                .skillName(
                                        reqSkill
                                )

                                .matched(
                                        false
                                )

                                .proficiencyLevel(
                                        null
                                )

                                .yearsOfExperience(
                                        0
                                )

                                .earnedWeight(
                                        0.0
                                )

                                .build()
                );
            }
        }


        double skillScore;


        if (
                reqSkills.isEmpty()
        ) {

            skillScore =
                    60.0;

        } else {

            double rawSkillScore =
                    (
                            earnedSkillPoints
                                    /
                            reqSkills.size()
                    )
                            *
                    60.0;


            skillScore =
                    Math.min(
                            60.0,

                            Math.round(
                                    rawSkillScore
                                            *
                                    100.0
                            )
                                    /
                            100.0
                    );
        }


        // ==========================================
        // 2. EXPERIENCE MATCH
        // MAXIMUM 25 POINTS
        // ==========================================

        int currentExp =
                empSkills
                        .stream()
                        .mapToInt(
                                employeeSkill ->

                                        employeeSkill
                                                .getYearsOfExperience()
                                                !=
                                                null

                                                ?

                                                employeeSkill
                                                        .getYearsOfExperience()

                                                :

                                                0
                        )
                        .max()
                        .orElse(
                                0
                        );


        int reqExp =
                job.getMinExperienceYears()
                        !=
                        null

                        ?

                        job.getMinExperienceYears()

                        :

                        0;


        double expScore;

        boolean experienceSatisfied;


        if (
                reqExp <= 0
        ) {

            expScore =
                    25.0;

            experienceSatisfied =
                    true;

        } else {

            double ratio =
                    Math.min(
                            1.0,

                            (double)
                                    currentExp
                                    /
                                    reqExp
                    );


            expScore =
                    Math.round(
                            ratio
                                    *
                            25.0
                                    *
                            100.0
                    )
                            /
                    100.0;


            experienceSatisfied =
                    currentExp
                            >=
                    reqExp;
        }


        // ==========================================
        // 3. CERTIFICATION MATCH
        // MAXIMUM 15 POINTS
        // ==========================================

        List<Certification> certifications =
                certificationRepository
                        .findByUserId(
                                employee.getId()
                        );


        double certificationScore =
                Math.min(
                        15.0,

                        certifications.size()
                                *
                        5.0
                );


        // ==========================================
        // OVERALL MATCH SCORE
        // ==========================================

        double overallScore =
                Math.min(

                        100.0,

                        Math.round(
                                (
                                        skillScore
                                                +
                                        expScore
                                                +
                                        certificationScore
                                )
                                        *
                                10.0
                        )
                                /
                        10.0
                );


        String compatibilityLevel =
                getCompatibilityLevel(
                        overallScore
                );


        // ==========================================
        // GAP ANALYSIS
        // ==========================================

        List<String> gapAnalysis =
                new ArrayList<>();


        if (
                !missingSkills
                        .isEmpty()
        ) {

            gapAnalysis.add(
                    "Missing "
                            +
                    missingSkills.size()
                            +
                    " required skill(s): "
                            +
                    String.join(
                            ", ",
                            missingSkills
                    )
                            +
                    "."
            );
        }


        if (
                !experienceSatisfied
        ) {

            gapAnalysis.add(
                    "Experience gap: Has "
                            +
                    currentExp
                            +
                    " years vs required "
                            +
                    reqExp
                            +
                    " years."
            );
        }


        if (
                gapAnalysis
                        .isEmpty()
        ) {

            gapAnalysis.add(
                    "Strong profile: Matches all core required skills and experience milestones."
            );
        }


        // ==========================================
        // RECOMMENDED COURSES
        // ==========================================

        List<CourseResponse>
                recommendedCourses =
                findCoursesForSkills(
                        missingSkills
                );


        // ==========================================
        // EXISTING JOB APPLICATION
        // ==========================================

        Optional<JobApplication>
                existingApplication =
                jobApplicationRepository
                        .findByApplicantIdAndJobPostingId(
                                employee.getId(),
                                job.getId()
                        );


        // ==========================================
        // RESPONSE
        // ==========================================

        return JobMatchResponse
                .builder()

                .jobId(
                        job.getId()
                )

                .jobTitle(
                        job.getTitle()
                )

                .jobDepartment(
                        job.getDepartment()
                )

                .jobLocation(
                        job.getLocation()
                )

                .employmentType(
                        job.getEmploymentType()
                )

                .requiredExperienceYears(
                        reqExp
                )

                .employeeId(
                        employee.getId()
                )

                .employeeName(
                        employee.getFirstName()
                                +
                        " "
                                +
                        employee.getLastName()
                )

                .employeeEmail(
                        employee.getEmail()
                )

                .overallMatchScore(
                        overallScore
                )

                .compatibilityLevel(
                        compatibilityLevel
                )

                .skillScore(
                        skillScore
                )

                .experienceScore(
                        expScore
                )

                .certificationsScore(
                        certificationScore
                )

                .skillDetails(
                        skillDetails
                )

                .matchedSkills(
                        matchedSkills
                )

                .missingSkills(
                        missingSkills
                )

                .currentExperienceYears(
                        currentExp
                )

                .experienceSatisfied(
                        experienceSatisfied
                )

                .gapAnalysis(
                        gapAnalysis
                )

                .recommendedCoursesToBridgeGap(
                        recommendedCourses
                )

                .alreadyApplied(
                        existingApplication
                                .isPresent()
                )

                .applicationStatus(
                        existingApplication
                                .map(
                                        JobApplication
                                                ::getStatus
                                )
                                .orElse(
                                        null
                                )
                )

                .build();
    }


    // ==========================================
    // COMPUTE CANDIDATE MATCH
    // ==========================================

    private CandidateMatchResponse
    computeCandidateMatch(
            User candidate,
            JobPosting job) {

        JobMatchResponse match =
                computeJobMatch(
                        candidate,
                        job
                );


        Optional<JobApplication>
                existingApplication =
                jobApplicationRepository
                        .findByApplicantIdAndJobPostingId(
                                candidate.getId(),
                                job.getId()
                        );


        return CandidateMatchResponse
                .builder()

                .candidateId(
                        candidate.getId()
                )

                .candidateName(
                        candidate.getFirstName()
                                +
                        " "
                                +
                        candidate.getLastName()
                )

                .candidateEmail(
                        candidate.getEmail()
                )

                .candidateEmployeeId(
                        candidate.getEmployeeId()
                )

                .department(
                        candidate.getDepartment()
                )

                .overallMatchScore(
                        match.getOverallMatchScore()
                )

                .compatibilityLevel(
                        match.getCompatibilityLevel()
                )

                .matchedSkillsCount(
                        match.getMatchedSkills()
                                .size()
                )

                .totalRequiredSkills(
                        match.getSkillDetails()
                                .size()
                )

                .matchedSkills(
                        match.getMatchedSkills()
                )

                .missingSkills(
                        match.getMissingSkills()
                )

                .experienceYears(
                        match.getCurrentExperienceYears()
                )

                .experienceSatisfied(
                        match.isExperienceSatisfied()
                )

                .applied(
                        existingApplication
                                .isPresent()
                )

                .applicationId(
                        existingApplication
                                .map(
                                        JobApplication
                                                ::getId
                                )
                                .orElse(
                                        null
                                )
                )

                .applicationStatus(
                        existingApplication
                                .map(
                                        JobApplication
                                                ::getStatus
                                )
                                .orElse(
                                        null
                                )
                )

                .build();
    }


    // ==========================================
    // PROFICIENCY WEIGHT
    // ==========================================

    private double getProficiencyWeight(
            ProficiencyLevel level) {

        if (
                level == null
        ) {

            return 0.6;
        }


        return switch (
                level
        ) {

            case EXPERT ->
                    1.1;

            case ADVANCED ->
                    1.0;

            case INTERMEDIATE ->
                    0.85;

            case BEGINNER ->
                    0.6;
        };
    }


    // ==========================================
    // COMPATIBILITY LEVEL
    // ==========================================

    private String getCompatibilityLevel(
            double score) {

        if (
                score >= 85.0
        ) {

            return "EXCELLENT";
        }


        if (
                score >= 70.0
        ) {

            return "GOOD";
        }


        if (
                score >= 50.0
        ) {

            return "MODERATE";
        }


        return "LOW";
    }


    // ==========================================
    // PARSE COMMA SEPARATED VALUES
    // ==========================================

    private List<String>
    parseCommaList(
            String value) {

        if (
                value == null
                ||
                value
                        .trim()
                        .isEmpty()
        ) {

            return Collections
                    .emptyList();
        }


        return Arrays
                .stream(
                        value.split(",")
                )
                .map(
                        String::trim
                )
                .filter(
                        valueItem ->
                                !valueItem
                                        .isEmpty()
                )
                .collect(
                        Collectors.toList()
                );
    }


    // ==========================================
    // FIND COURSES FOR MISSING SKILLS
    // ==========================================

    private List<CourseResponse>
    findCoursesForSkills(
            List<String> missingSkills) {

        if (
                missingSkills
                        .isEmpty()
        ) {

            return Collections
                    .emptyList();
        }


        List<Course> allCourses =
                courseRepository
                        .findAll();


        List<Course> matchedCourses =
                new ArrayList<>();


        for (
                String skill
                :
                missingSkills
        ) {

            String skillTerm =
                    skill.toLowerCase();


            for (
                    Course course
                    :
                    allCourses
            ) {

                boolean matches =

                        (
                                course.getTitle()
                                        !=
                                        null

                                &&

                                course.getTitle()
                                        .toLowerCase()
                                        .contains(
                                                skillTerm
                                        )
                        )

                        ||

                        (
                                course.getDescription()
                                        !=
                                        null

                                &&

                                course.getDescription()
                                        .toLowerCase()
                                        .contains(
                                                skillTerm
                                        )
                        )

                        ||

                        (
                                course.getCategory()
                                        !=
                                        null

                                &&

                                course.getCategory()
                                        .toLowerCase()
                                        .contains(
                                                skillTerm
                                        )
                        );


                if (
                        matches
                        &&
                        !matchedCourses
                                .contains(
                                        course
                                )
                ) {

                    matchedCourses.add(
                            course
                    );
                }
            }
        }


        return matchedCourses
                .stream()
                .limit(
                        5
                )
                .map(
                        course ->

                                CourseResponse
                                        .builder()

                                        .id(
                                                course.getId()
                                        )

                                        .title(
                                                course.getTitle()
                                        )

                                        .description(
                                                course.getDescription()
                                        )

                                        .category(
                                                course.getCategory()
                                        )

                                        .level(
                                                course.getLevel()
                                        )

                                        .durationHours(
                                                course.getDurationHours()
                                        )

                                        .instructor(
                                                course.getInstructor()
                                        )

                                        .createdAt(
                                                course.getCreatedAt()
                                        )

                                        .updatedAt(
                                                course.getUpdatedAt()
                                        )

                                        .build()
                )
                .collect(
                        Collectors.toList()
                );
    }
}
