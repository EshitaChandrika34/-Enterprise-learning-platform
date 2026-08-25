package com.enterpriselearning.repository;

import com.enterpriselearning.entity.JobPosting;
import com.enterpriselearning.entity.JobPostingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    List<JobPosting> findByStatus(JobPostingStatus status);

    List<JobPosting> findByDepartment(String department);

    List<JobPosting> findByStatusAndDepartment(JobPostingStatus status, String department);

    @Query("SELECT j FROM JobPosting j WHERE " +
           "(:status IS NULL OR j.status = :status) AND " +
           "(:department IS NULL OR LOWER(j.department) = LOWER(:department)) AND " +
           "(:search IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(j.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<JobPosting> searchJobPostings(
            @Param("status") JobPostingStatus status,
            @Param("department") String department,
            @Param("search") String search);
}
