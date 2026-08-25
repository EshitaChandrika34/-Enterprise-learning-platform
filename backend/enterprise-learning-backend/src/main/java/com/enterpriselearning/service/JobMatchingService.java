package com.enterpriselearning.service;

import com.enterpriselearning.dto.response.CandidateMatchResponse;
import com.enterpriselearning.dto.response.JobMatchResponse;

import java.util.List;

public interface JobMatchingService {

    JobMatchResponse matchEmployeeWithJob(Long employeeId, Long jobId);

    JobMatchResponse matchMyProfileWithJob(String userEmail, Long jobId);

    List<JobMatchResponse> findMatchedJobsForEmployee(Long employeeId, Double minScore);

    List<JobMatchResponse> findMatchedJobsForMe(String userEmail, Double minScore);

    List<CandidateMatchResponse> findMatchedCandidatesForJob(Long jobId, Double minScore);
}
