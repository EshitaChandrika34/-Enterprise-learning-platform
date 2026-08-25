package com.enterpriselearning.service;

import com.enterpriselearning.dto.request.ApplicationStatusUpdateRequest;
import com.enterpriselearning.dto.request.JobApplicationRequest;
import com.enterpriselearning.dto.request.JobPostingRequest;
import com.enterpriselearning.dto.response.JobApplicationResponse;
import com.enterpriselearning.dto.response.JobPostingResponse;
import com.enterpriselearning.entity.JobPostingStatus;

import java.util.List;

public interface JobPortalService {

    // Job Posting Management
    JobPostingResponse createJobPosting(JobPostingRequest request, String postedByEmail);
    List<JobPostingResponse> getAllJobPostings(JobPostingStatus status, String department, String search);
    JobPostingResponse getJobPostingById(Long id);
    JobPostingResponse updateJobPosting(Long id, JobPostingRequest request);
    void deleteJobPosting(Long id);

    // Job Application Management
    JobApplicationResponse applyForJob(JobApplicationRequest request, String applicantEmail);
    List<JobApplicationResponse> getMyApplications(String applicantEmail);
    List<JobApplicationResponse> getApplicationsByJob(Long jobPostingId);
    List<JobApplicationResponse> getApplicationsByEmployee(Long employeeId);
    JobApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatusUpdateRequest request);
    void withdrawApplication(Long applicationId, String applicantEmail);
}
