package com.enterpriselearning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionCriteriaResponse {
    private Long id;
    private String name;
    private String description;
    private Integer minimumScore;
    private String targetRole;
    private String requiredDepartment;
    private Integer minExperienceYears;
    private String requiredSkills;
    private List<String> requiredSkillList;
    private Integer minCompletedCourses;
    private Integer minCertifications;
    private Boolean active;
}
