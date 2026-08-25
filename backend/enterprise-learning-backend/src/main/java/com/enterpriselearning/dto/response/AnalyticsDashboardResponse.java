package com.enterpriselearning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDashboardResponse {
    private EmployeeStatsResponse employeeStats;
    private SkillStatsResponse skillStats;
    private LearningStatsResponse learningStats;
    private CertificationStatsResponse certificationStats;
}
