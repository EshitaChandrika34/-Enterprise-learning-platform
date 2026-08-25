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
public class CandidateMatchResponse {
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    private String candidateEmployeeId;
    private String department;
    
    private double overallMatchScore;
    private String compatibilityLevel;
    
    private int matchedSkillsCount;
    private int totalRequiredSkills;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    
    private Integer experienceYears;
    private boolean experienceSatisfied;
    
    private boolean applied;
    private Long applicationId;
    private JobApplicationStatus applicationStatus;
}
