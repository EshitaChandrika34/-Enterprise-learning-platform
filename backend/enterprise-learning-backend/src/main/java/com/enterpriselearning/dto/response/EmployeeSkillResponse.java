package com.enterpriselearning.dto.response;

import com.enterpriselearning.entity.ProficiencyLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSkillResponse {
    private Long id;
    private Long userId;
    private String employeeName;
    private String employeeId;
    private Long skillId;
    private String skillName;
    private String skillCategory;
    private ProficiencyLevel proficiencyLevel;
    private boolean verified;
    private Integer yearsOfExperience;
    private LocalDateTime assignedDate;
}
