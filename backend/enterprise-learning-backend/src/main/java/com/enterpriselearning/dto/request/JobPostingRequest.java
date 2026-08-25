package com.enterpriselearning.dto.request;

import com.enterpriselearning.entity.JobPostingStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingRequest {

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Department is required")
    private String department;

    private String location;

    private String employmentType; // Full-time, Remote, Hybrid, Contract

    @NotBlank(message = "Job description is required")
    private String description;

    private String requiredSkills; // Comma-separated list

    private Integer minExperienceYears;

    private LocalDate deadline;

    @Builder.Default
    private JobPostingStatus status = JobPostingStatus.OPEN;
}
