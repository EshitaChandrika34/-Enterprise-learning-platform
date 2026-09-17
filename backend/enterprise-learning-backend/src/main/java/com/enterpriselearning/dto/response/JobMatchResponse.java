package com.enterpriselearning.dto.response;

import com.enterpriselearning.entity.JobApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobMatchResponse {

    // ============================================================
    // JOB INFORMATION
    // ============================================================

    private Long jobId;

    private String jobTitle;

    private String jobDepartment;

    private String jobLocation;

    private String employmentType;

    private Integer requiredExperienceYears;


    /*
     * Original comma-separated required skills.
     *
     * Example:
     *
     * "Java, Spring Boot, SQL & Database Design"
     */
    private String requiredSkills;


    /*
     * Easy-to-display list of required skills.
     *
     * Example:
     *
     * [
     *   "Java",
     *   "Spring Boot",
     *   "SQL & Database Design"
     * ]
     */
    private List<String> requiredSkillList;


    // ============================================================
    // EMPLOYEE INFORMATION
    // ============================================================

    private Long employeeId;

    private String employeeName;

    private String employeeEmail;


    // ============================================================
    // OVERALL MATCH
    // ============================================================

    private double overallMatchScore;

    /*
     * EXCELLENT >= 85
     * GOOD       >= 70
     * MODERATE   >= 50
     * LOW        < 50
     */
    private String compatibilityLevel;


    // ============================================================
    // COMPONENT SCORES
    // ============================================================

    /*
     * Maximum 60 points.
     */
    private double skillScore;


    /*
     * Maximum 25 points.
     */
    private double experienceScore;


    /*
     * Maximum 15 points.
     */
    private double certificationsScore;


    // ============================================================
    // DETAILED SKILL BREAKDOWN
    // ============================================================

    /*
     * Contains one entry for every required skill.
     *
     * Each entry tells the frontend:
     *
     * - skill name
     * - whether employee has it
     * - proficiency
     * - experience
     * - earned weight
     */
    private List<SkillMatchDetail> skillDetails;


    /*
     * Skills the employee already has.
     */
    private List<String> matchedSkills;


    /*
     * Required skills the employee is missing.
     */
    private List<String> missingSkills;


    // ============================================================
    // EXPERIENCE
    // ============================================================

    private Integer currentExperienceYears;

    private boolean experienceSatisfied;


    // ============================================================
    // GAP ANALYSIS
    // ============================================================

    private List<String> gapAnalysis;


    // ============================================================
    // COURSE RECOMMENDATIONS
    // ============================================================

    private List<CourseResponse> recommendedCoursesToBridgeGap;


    // ============================================================
    // APPLICATION INFORMATION
    // ============================================================

    private boolean alreadyApplied;

    private JobApplicationStatus applicationStatus;
}