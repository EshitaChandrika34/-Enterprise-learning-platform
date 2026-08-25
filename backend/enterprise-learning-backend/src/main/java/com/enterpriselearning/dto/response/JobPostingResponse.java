package com.enterpriselearning.dto.response;

import com.enterpriselearning.entity.JobPostingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingResponse {
    private Long id;
    private String title;
    private String department;
    private String location;
    private String employmentType;
    private String description;
    private String requiredSkills;
    private List<String> requiredSkillList;
    private Integer minExperienceYears;
    private JobPostingStatus status;
    private Long postedById;
    private String postedByName;
    private LocalDate deadline;
    private long applicationCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
