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
public class SkillRecommendationResponse {
    private Long userId;
    private String employeeName;
    private String employeeId;
    private String targetRole;
    private List<String> currentSkills;
    private List<String> recommendedSkills;
    private List<String> missingSkills;
    private List<CourseResponse> suggestedCourses;
    private double matchPercentage;
}
