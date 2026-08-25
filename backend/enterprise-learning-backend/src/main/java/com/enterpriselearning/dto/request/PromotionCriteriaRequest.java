package com.enterpriselearning.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionCriteriaRequest {

    @NotBlank(message = "Criteria name is required")
    private String name;

    private String description;

    @NotNull(message = "Minimum score is required")
    @Min(value = 1, message = "Minimum score must be at least 1")
    @Max(value = 100, message = "Minimum score cannot exceed 100")
    private Integer minimumScore;

    private String targetRole;

    private String requiredDepartment;

    private Integer minExperienceYears;

    private String requiredSkills; // Comma-separated list

    private Integer minCompletedCourses;

    private Integer minCertifications;

    @Builder.Default
    private Boolean active = true;
}
