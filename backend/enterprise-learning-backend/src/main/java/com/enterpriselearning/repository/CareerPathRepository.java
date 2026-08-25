package com.enterpriselearning.repository;

import com.enterpriselearning.entity.CareerPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CareerPathRepository extends JpaRepository<CareerPath, Long> {
    Optional<CareerPath> findByTitleIgnoreCase(String title);
    List<CareerPath> findByDepartment(String department);
    List<CareerPath> findByTargetRole(String targetRole);
    List<CareerPath> findByTargetRoleIgnoreCase(String targetRole);
}
