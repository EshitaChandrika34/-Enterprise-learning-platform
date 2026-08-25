package com.enterpriselearning.dto.response;

import com.enterpriselearning.entity.ProficiencyLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillMatchDetail {
    private String skillName;
    private boolean matched;
    private ProficiencyLevel proficiencyLevel;
    private Integer yearsOfExperience;
    private double earnedWeight; // e.g. 1.0 for ADVANCED, 0.85 for INTERMEDIATE, 0.6 for BEGINNER, 0 for missing
}
