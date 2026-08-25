package com.enterpriselearning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerPathResponse {
    private Long id;
    private String title;
    private String targetRole;
    private String department;
    private String description;
    private String requiredSkills;
    private List<String> requiredSkillList;
    private String recommendedCourses;
    private List<String> recommendedCourseList;
    private Integer minExperienceYears;
    private LocalDateTime createdAt;
}
