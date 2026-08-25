package com.enterpriselearning.service;

import com.enterpriselearning.dto.request.PromotionCriteriaRequest;
import com.enterpriselearning.dto.response.PromotionCriteriaResponse;
import com.enterpriselearning.dto.response.PromotionEvaluationResponse;
import com.enterpriselearning.entity.PromotionCriteria;

import java.util.List;

public interface PromotionCriteriaService {

    // Entity-based methods (Preserved)
    PromotionCriteria create(PromotionCriteria criteria);
    List<PromotionCriteria> getAll();
    List<PromotionCriteria> getActive();
    PromotionCriteria getById(Long id);
    PromotionCriteria update(Long id, PromotionCriteria criteria);
    void delete(Long id);

    // DTO-based methods
    PromotionCriteriaResponse createCriteria(PromotionCriteriaRequest request);
    List<PromotionCriteriaResponse> getAllCriteria();
    List<PromotionCriteriaResponse> getActiveCriteria();
    PromotionCriteriaResponse getCriteriaById(Long id);
    PromotionCriteriaResponse updateCriteria(Long id, PromotionCriteriaRequest request);

    // Evaluation Engine methods
    PromotionEvaluationResponse evaluateEmployee(Long employeeId, Long criteriaId);
    PromotionEvaluationResponse evaluateMyEligibility(String userEmail, Long criteriaId);
    List<PromotionEvaluationResponse> evaluateEmployeeAgainstAll(Long employeeId);
    List<PromotionEvaluationResponse> evaluateMyEligibilityAgainstAll(String userEmail);
}
