package com.enterpriselearning.repository;

import com.enterpriselearning.entity.PromotionCriteria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionCriteriaRepository
        extends JpaRepository<PromotionCriteria, Long> {

    List<PromotionCriteria> findByActiveTrue();
    List<PromotionCriteria> findByTargetRoleIgnoreCase(String targetRole);
    List<PromotionCriteria> findByRequiredDepartmentIgnoreCase(String department);
}
