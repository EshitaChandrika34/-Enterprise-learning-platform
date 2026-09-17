package com.enterpriselearning.service.impl;

import com.enterpriselearning.dto.request.ApplicationStatusUpdateRequest;
import com.enterpriselearning.dto.request.JobApplicationRequest;
import com.enterpriselearning.dto.request.JobPostingRequest;
import com.enterpriselearning.dto.response.JobApplicationResponse;
import com.enterpriselearning.dto.response.JobPostingResponse;
import com.enterpriselearning.entity.*;
import com.enterpriselearning.exception.BadRequestException;
import com.enterpriselearning.exception.ResourceNotFoundException;
import com.enterpriselearning.repository.EmployeeSkillRepository;
import com.enterpriselearning.repository.JobApplicationRepository;
import com.enterpriselearning.repository.JobPostingRepository;
import com.enterpriselearning.repository.UserRepository;
import com.enterpriselearning.service.JobPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobPortalServiceImpl implements JobPortalService {

    private final JobPostingRepository jobPostingRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final EmployeeSkillRepository employeeSkillRepository;

    @Override
    @Transactional
    public JobPostingResponse createJobPosting(
            JobPostingRequest request,
            String postedByEmail) {

        User postedBy = null;

        if (postedByEmail != null) {
            postedBy =
                    userRepository.findByEmail(postedByEmail)
                            .orElse(null);
        }

        JobPosting jobPosting =
                JobPosting.builder()
                        .title(request.getTitle())
                        .department(request.getDepartment())
                        .location(request.getLocation())
                        .employmentType(request.getEmploymentType())
                        .description(request.getDescription())
                        .requiredSkills(request.getRequiredSkills())
                        .minExperienceYears(
                                request.getMinExperienceYears()
                        )
                        .deadline(request.getDeadline())
                        .status(
                                request.getStatus() != null
                                        ? request.getStatus()
                                        : JobPostingStatus.OPEN
                        )
                        .postedBy(postedBy)
                        .build();

        JobPosting saved =
                jobPostingRepository.save(jobPosting);

        return mapToJobPostingResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobPostingResponse> getAllJobPostings(
            JobPostingStatus status,
            String department,
            String search) {

        List<JobPosting> list =
                jobPostingRepository.searchJobPostings(
                        status,
                        department,
                        search
                );

        return list.stream()
                .map(this::mapToJobPostingResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public JobPostingResponse getJobPostingById(Long id) {

        JobPosting job =
                jobPostingRepository.findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Job posting not found with id: "
                                                        + id
                                        )
                        );

        return mapToJobPostingResponse(job);
    }

    @Override
    @Transactional
    public JobPostingResponse updateJobPosting(
            Long id,
            JobPostingRequest request) {

        JobPosting job =
                jobPostingRepository.findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Job posting not found with id: "
                                                        + id
                                        )
                        );

        job.setTitle(request.getTitle());
        job.setDepartment(request.getDepartment());
        job.setLocation(request.getLocation());
        job.setEmploymentType(request.getEmploymentType());
        job.setDescription(request.getDescription());
        job.setRequiredSkills(request.getRequiredSkills());
        job.setMinExperienceYears(
                request.getMinExperienceYears()
        );
        job.setDeadline(request.getDeadline());

        if (request.getStatus() != null) {
            job.setStatus(
                    request.getStatus()
            );
        }

        JobPosting updated =
                jobPostingRepository.save(job);

        return mapToJobPostingResponse(updated);
    }

    @Override
    @Transactional
    public void deleteJobPosting(Long id) {

        JobPosting job =
                jobPostingRepository.findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Job posting not found with id: "
                                                        + id
                                        )
                        );

        jobPostingRepository.delete(job);
    }

    @Override
    @Transactional
    public JobApplicationResponse applyForJob(
            JobApplicationRequest request,
            String applicantEmail) {

        User applicant =
                userRepository.findByEmail(applicantEmail)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "User not found with email: "
                                                        + applicantEmail
                                        )
                        );

        JobPosting job =
                jobPostingRepository.findById(
                                request.getJobPostingId()
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Job posting not found with id: "
                                                        + request.getJobPostingId()
                                        )
                        );

        if (job.getStatus() == JobPostingStatus.CLOSED) {

            throw new BadRequestException(
                    "This job posting is closed and is no longer accepting applications."
            );
        }

        if (
                jobApplicationRepository
                        .existsByApplicantIdAndJobPostingId(
                                applicant.getId(),
                                job.getId()
                        )
        ) {

            throw new BadRequestException(
                    "You have already applied for this position: "
                            + job.getTitle()
            );
        }

        /*
         * Calculate the current compatibility score.
         */
        double matchScore =
                calculateBasicMatchScore(
                        applicant,
                        job
                );

        /*
         * Business rule:
         *
         * An employee must have at least 40% compatibility
         * to apply for an internal job.
         *
         * This prevents a 0% profile from applying.
         */
        if (
                applicant.getRole() == Role.EMPLOYEE
                        && matchScore < 40.0
        ) {

            throw new BadRequestException(
                    "You are not currently eligible to apply for this job. "
                            + "Your current match score is "
                            + formatScore(matchScore)
                            + "%. Improve your skills or experience first."
            );
        }

        JobApplication application =
                JobApplication.builder()
                        .jobPosting(job)
                        .applicant(applicant)
                        .status(
                                JobApplicationStatus.APPLIED
                        )
                        .matchScore(matchScore)
                        .coverNote(
                                request.getCoverNote()
                        )
                        .build();

        JobApplication saved =
                jobApplicationRepository.save(
                        application
                );

        return mapToApplicationResponse(
                saved
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplicationResponse> getMyApplications(
            String applicantEmail) {

        User applicant =
                userRepository.findByEmail(applicantEmail)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "User not found with email: "
                                                        + applicantEmail
                                        )
                        );

        return jobApplicationRepository
                .findByApplicantId(
                        applicant.getId()
                )
                .stream()
                .map(this::mapToApplicationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplicationResponse> getApplicationsByJob(
            Long jobPostingId) {

        if (
                !jobPostingRepository.existsById(
                        jobPostingId
                )
        ) {

            throw new ResourceNotFoundException(
                    "Job posting not found with id: "
                            + jobPostingId
            );
        }

        return jobApplicationRepository
                .findByJobPostingId(jobPostingId)
                .stream()
                .map(this::mapToApplicationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplicationResponse> getApplicationsByEmployee(
            Long employeeId) {

        if (
                !userRepository.existsById(
                        employeeId
                )
        ) {

            throw new ResourceNotFoundException(
                    "Employee not found with id: "
                            + employeeId
            );
        }

        return jobApplicationRepository
                .findByApplicantId(employeeId)
                .stream()
                .map(this::mapToApplicationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobApplicationResponse updateApplicationStatus(
            Long applicationId,
            ApplicationStatusUpdateRequest request) {

        JobApplication app =
                jobApplicationRepository.findById(
                                applicationId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Job application not found with id: "
                                                        + applicationId
                                        )
                        );

        app.setStatus(
                request.getStatus()
        );

        JobApplication updated =
                jobApplicationRepository.save(
                        app
                );

        return mapToApplicationResponse(
                updated
        );
    }

    @Override
    @Transactional
    public void withdrawApplication(
            Long applicationId,
            String applicantEmail) {

        JobApplication app =
                jobApplicationRepository.findById(
                                applicationId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Job application not found with id: "
                                                        + applicationId
                                        )
                        );

        User user =
                userRepository.findByEmail(
                                applicantEmail
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "User not found with email: "
                                                        + applicantEmail
                                        )
                        );

        /*
         * Only the applicant or ADMIN can withdraw.
         */
        if (
                !app.getApplicant()
                        .getId()
                        .equals(
                                user.getId()
                        )
                        &&
                user.getRole() != Role.ADMIN
        ) {

            throw new BadRequestException(
                    "You are not authorized to withdraw this application."
            );
        }

        jobApplicationRepository.delete(
                app
        );
    }

    /*
     * ============================================================
     * MATCH SCORE
     * ============================================================
     *
     * Current implementation calculates:
     *
     * Skill compatibility = matched required skills / total skills
     *
     * Experience is also considered where the job defines a
     * minimum experience requirement.
     *
     * Final score:
     *
     * 70% Skill Match
     * 30% Experience Match
     *
     * If the job does not specify experience, the skill score
     * becomes the full score.
     */
    private double calculateBasicMatchScore(
            User applicant,
            JobPosting job) {

        List<String> reqSkills =
                parseCommaList(
                        job.getRequiredSkills()
                );

        List<EmployeeSkill> empSkills =
                employeeSkillRepository
                        .findByUserId(
                                applicant.getId()
                        );

        Set<String> empSkillNames =
                empSkills.stream()
                        .filter(
                                es ->
                                        es.getSkill() != null
                                                &&
                                        es.getSkill()
                                                .getName() != null
                        )
                        .map(
                                es ->
                                        es.getSkill()
                                                .getName()
                                                .trim()
                                                .toLowerCase()
                        )
                        .collect(
                                Collectors.toSet()
                        );

        double skillScore;

        if (reqSkills.isEmpty()) {

            skillScore = 100.0;

        } else {

            long matchCount =
                    reqSkills.stream()
                            .filter(
                                    skill ->
                                            empSkillNames.contains(
                                                    skill.toLowerCase()
                                            )
                            )
                            .count();

            skillScore =
                    (
                            (double) matchCount
                                    / reqSkills.size()
                    )
                            * 100.0;
        }

        /*
         * Calculate experience score.
         *
         * We use the highest years of experience stored
         * in the employee's skill records.
         */
        int currentExperience =
                empSkills.stream()
                        .mapToInt(
                                es ->
                                        es.getYearsOfExperience() != null
                                                ?
                                                es.getYearsOfExperience()
                                                :
                                                0
                        )
                        .max()
                        .orElse(0);

        Integer requiredExperience =
                job.getMinExperienceYears();

        double experienceScore;

        if (
                requiredExperience == null
                        ||
                requiredExperience <= 0
        ) {

            experienceScore = 100.0;

        } else {

            experienceScore =
                    Math.min(
                            100.0,
                            (
                                    (double) currentExperience
                                            / requiredExperience
                            )
                                    * 100.0
                    );
        }

        /*
         * If the job has no required skills and no experience
         * requirement, the employee is fully compatible.
         */
        if (
                reqSkills.isEmpty()
                        &&
                (
                        requiredExperience == null
                                ||
                        requiredExperience <= 0
                )
        ) {

            return 100.0;
        }

        /*
         * Weighted final match score.
         */
        double finalScore =
                (
                        skillScore * 0.70
                                +
                        experienceScore * 0.30
                );

        return Math.round(
                finalScore * 10.0
        ) / 10.0;
    }

    private String formatScore(
            double score) {

        return String.format(
                Locale.US,
                "%.1f",
                score
        );
    }

    private List<String> parseCommaList(
            String value) {

        if (
                value == null
                        ||
                value.trim().isEmpty()
        ) {

            return Collections.emptyList();
        }

        return Arrays.stream(
                        value.split(",")
                )
                .map(String::trim)
                .filter(
                        s ->
                                !s.isEmpty()
                )
                .collect(
                        Collectors.toList()
                );
    }

    private JobPostingResponse mapToJobPostingResponse(
            JobPosting job) {

        long appCount =
                jobApplicationRepository
                        .countByJobPostingId(
                                job.getId()
                        );

        return JobPostingResponse.builder()
                .id(
                        job.getId()
                )
                .title(
                        job.getTitle()
                )
                .department(
                        job.getDepartment()
                )
                .location(
                        job.getLocation()
                )
                .employmentType(
                        job.getEmploymentType()
                )
                .description(
                        job.getDescription()
                )
                .requiredSkills(
                        job.getRequiredSkills()
                )
                .requiredSkillList(
                        parseCommaList(
                                job.getRequiredSkills()
                        )
                )
                .minExperienceYears(
                        job.getMinExperienceYears()
                )
                .status(
                        job.getStatus()
                )
                .postedById(
                        job.getPostedBy() != null
                                ?
                                job.getPostedBy()
                                        .getId()
                                :
                                null
                )
                .postedByName(
                        job.getPostedBy() != null
                                ?
                                job.getPostedBy()
                                        .getFirstName()
                                        + " "
                                        + job.getPostedBy()
                                        .getLastName()
                                :
                                "Human Resources"
                )
                .deadline(
                        job.getDeadline()
                )
                .applicationCount(
                        appCount
                )
                .createdAt(
                        job.getCreatedAt()
                )
                .updatedAt(
                        job.getUpdatedAt()
                )
                .build();
    }

    private JobApplicationResponse mapToApplicationResponse(
            JobApplication app) {

        return JobApplicationResponse.builder()
                .id(
                        app.getId()
                )
                .jobPostingId(
                        app.getJobPosting()
                                .getId()
                )
                .jobTitle(
                        app.getJobPosting()
                                .getTitle()
                )
                .jobDepartment(
                        app.getJobPosting()
                                .getDepartment()
                )
                .applicantId(
                        app.getApplicant()
                                .getId()
                )
                .applicantName(
                        app.getApplicant()
                                .getFirstName()
                                + " "
                                + app.getApplicant()
                                .getLastName()
                )
                .applicantEmail(
                        app.getApplicant()
                                .getEmail()
                )
                .applicantEmployeeId(
                        app.getApplicant()
                                .getEmployeeId()
                )
                .status(
                        app.getStatus()
                )
                .matchScore(
                        app.getMatchScore()
                )
                .coverNote(
                        app.getCoverNote()
                )
                .appliedDate(
                        app.getAppliedDate()
                )
                .updatedAt(
                        app.getUpdatedAt()
                )
                .build();
    }
}