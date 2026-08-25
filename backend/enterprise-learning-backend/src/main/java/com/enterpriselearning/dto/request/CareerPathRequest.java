package com.enterpriselearning.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerPathRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String targetRole;

    private String department;

    private String description;

    private String requiredSkills; // Comma-separated list of required skills

    private String recommendedCourses; // Comma-separated list of courses

    private Integer minExperienceYears;
}
