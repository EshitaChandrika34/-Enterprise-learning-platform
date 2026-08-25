package com.enterpriselearning.dto.response;

import com.enterpriselearning.entity.JobApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationResponse {
    private Long id;
    private Long jobPostingId;
    private String jobTitle;
    private String jobDepartment;
    private Long applicantId;
    private String applicantName;
    private String applicantEmail;
    private String applicantEmployeeId;
    private JobApplicationStatus status;
    private Double matchScore;
    private String coverNote;
    private LocalDateTime appliedDate;
    private LocalDateTime updatedAt;
}
