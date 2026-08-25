package com.enterpriselearning.repository;

import com.enterpriselearning.entity.JobApplication;
import com.enterpriselearning.entity.JobApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByApplicantId(Long applicantId);

    List<JobApplication> findByJobPostingId(Long jobPostingId);

    List<JobApplication> findByJobPostingIdAndStatus(Long jobPostingId, JobApplicationStatus status);

    Optional<JobApplication> findByApplicantIdAndJobPostingId(Long applicantId, Long jobPostingId);

    boolean existsByApplicantIdAndJobPostingId(Long applicantId, Long jobPostingId);

    long countByJobPostingId(Long jobPostingId);

    long countByStatus(JobApplicationStatus status);
}
