package com.enterpriselearning.controller;

import com.enterpriselearning.dto.request.ApplicationStatusUpdateRequest;
import com.enterpriselearning.dto.request.JobApplicationRequest;
import com.enterpriselearning.dto.request.JobPostingRequest;
import com.enterpriselearning.dto.response.*;
import com.enterpriselearning.entity.JobPostingStatus;
import com.enterpriselearning.service.JobMatchingService;
import com.enterpriselearning.service.JobPortalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobPortalController {

    private final JobPortalService jobPortalService;
    private final JobMatchingService jobMatchingService;

    // --- Job Posting Endpoints ---

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<JobPostingResponse>> createJobPosting(
            @Valid @RequestBody JobPostingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        JobPostingResponse response = jobPortalService.createJobPosting(request, userDetails != null ? userDetails.getUsername() : null);
        return new ResponseEntity<>(ApiResponse.success("Job posting created successfully", response), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobPostingResponse>>> getAllJobPostings(
            @RequestParam(required = false) JobPostingStatus status,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String search) {
        List<JobPostingResponse> response = jobPortalService.getAllJobPostings(status, department, search);
        return ResponseEntity.ok(ApiResponse.success("Job postings retrieved successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobPostingResponse>> getJobPostingById(@PathVariable Long id) {
        JobPostingResponse response = jobPortalService.getJobPostingById(id);
        return ResponseEntity.ok(ApiResponse.success("Job posting retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<JobPostingResponse>> updateJobPosting(
            @PathVariable Long id,
            @Valid @RequestBody JobPostingRequest request) {
        JobPostingResponse response = jobPortalService.updateJobPosting(id, request);
        return ResponseEntity.ok(ApiResponse.success("Job posting updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteJobPosting(@PathVariable Long id) {
        jobPortalService.deleteJobPosting(id);
        return ResponseEntity.ok(ApiResponse.success("Job posting deleted successfully", null));
    }

    // --- Job Application Endpoints ---

    @PostMapping("/apply")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<JobApplicationResponse>> applyForJob(
            @Valid @RequestBody JobApplicationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        JobApplicationResponse response = jobPortalService.applyForJob(request, userDetails.getUsername());
        return new ResponseEntity<>(ApiResponse.success("Job application submitted successfully", response), HttpStatus.CREATED);
    }

    @GetMapping("/my-applications")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<JobApplicationResponse>>> getMyApplications(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<JobApplicationResponse> list = jobPortalService.getMyApplications(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("My job applications retrieved successfully", list));
    }

    @GetMapping("/{id}/applications")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<List<JobApplicationResponse>>> getApplicationsByJob(@PathVariable Long id) {
        List<JobApplicationResponse> list = jobPortalService.getApplicationsByJob(id);
        return ResponseEntity.ok(ApiResponse.success("Job applications retrieved successfully", list));
    }

    @GetMapping("/employee/{employeeId}/applications")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<List<JobApplicationResponse>>> getApplicationsByEmployee(@PathVariable Long employeeId) {
        List<JobApplicationResponse> list = jobPortalService.getApplicationsByEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.success("Employee job applications retrieved successfully", list));
    }

    @PutMapping("/applications/{applicationId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<JobApplicationResponse>> updateApplicationStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody ApplicationStatusUpdateRequest request) {
        JobApplicationResponse response = jobPortalService.updateApplicationStatus(applicationId, request);
        return ResponseEntity.ok(ApiResponse.success("Application status updated successfully", response));
    }

    @DeleteMapping("/applications/{applicationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<Void>> withdrawApplication(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        jobPortalService.withdrawApplication(applicationId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Job application withdrawn successfully", null));
    }

    // --- Job Matching Endpoints ---

    @GetMapping("/{id}/match")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<JobMatchResponse>> matchMyProfileWithJob(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        JobMatchResponse response = jobMatchingService.matchMyProfileWithJob(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success("Job match analysis completed successfully", response));
    }

    @GetMapping("/{id}/match/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<JobMatchResponse>> matchEmployeeWithJob(
            @PathVariable Long id,
            @PathVariable Long employeeId) {
        JobMatchResponse response = jobMatchingService.matchEmployeeWithJob(employeeId, id);
        return ResponseEntity.ok(ApiResponse.success("Employee job match analysis completed successfully", response));
    }

    @GetMapping("/matched")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<JobMatchResponse>>> findMatchedJobsForMe(
            @RequestParam(required = false) Double minScore,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<JobMatchResponse> matches = jobMatchingService.findMatchedJobsForMe(userDetails.getUsername(), minScore);
        return ResponseEntity.ok(ApiResponse.success("Ranked matched jobs retrieved successfully", matches));
    }

    @GetMapping("/matched/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<List<JobMatchResponse>>> findMatchedJobsForEmployee(
            @PathVariable Long employeeId,
            @RequestParam(required = false) Double minScore) {
        List<JobMatchResponse> matches = jobMatchingService.findMatchedJobsForEmployee(employeeId, minScore);
        return ResponseEntity.ok(ApiResponse.success("Employee matched jobs retrieved successfully", matches));
    }

    @GetMapping("/{id}/matched-candidates")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<List<CandidateMatchResponse>>> findMatchedCandidatesForJob(
            @PathVariable Long id,
            @RequestParam(required = false) Double minScore) {
        List<CandidateMatchResponse> candidates = jobMatchingService.findMatchedCandidatesForJob(id, minScore);
        return ResponseEntity.ok(ApiResponse.success("Ranked candidate matches retrieved successfully", candidates));
    }
}
