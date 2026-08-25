package com.enterpriselearning.controller;

import com.enterpriselearning.dto.request.PromotionCriteriaRequest;
import com.enterpriselearning.dto.response.ApiResponse;
import com.enterpriselearning.dto.response.PromotionCriteriaResponse;
import com.enterpriselearning.dto.response.PromotionEvaluationResponse;
import com.enterpriselearning.service.PromotionCriteriaService;
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
@RequestMapping("/api/promotion-criteria")
@RequiredArgsConstructor
public class PromotionCriteriaController {

    private final PromotionCriteriaService promotionCriteriaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<PromotionCriteriaResponse>> create(
            @Valid @RequestBody PromotionCriteriaRequest request) {
        PromotionCriteriaResponse response = promotionCriteriaService.createCriteria(request);
        return new ResponseEntity<>(ApiResponse.success("Promotion criteria created successfully", response), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PromotionCriteriaResponse>>> getAll() {
        List<PromotionCriteriaResponse> list = promotionCriteriaService.getAllCriteria();
        return ResponseEntity.ok(ApiResponse.success("Promotion criteria list retrieved successfully", list));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PromotionCriteriaResponse>>> getActive() {
        List<PromotionCriteriaResponse> list = promotionCriteriaService.getActiveCriteria();
        return ResponseEntity.ok(ApiResponse.success("Active promotion criteria retrieved successfully", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PromotionCriteriaResponse>> getById(@PathVariable Long id) {
        PromotionCriteriaResponse response = promotionCriteriaService.getCriteriaById(id);
        return ResponseEntity.ok(ApiResponse.success("Promotion criteria retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<PromotionCriteriaResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody PromotionCriteriaRequest request) {
        PromotionCriteriaResponse response = promotionCriteriaService.updateCriteria(id, request);
        return ResponseEntity.ok(ApiResponse.success("Promotion criteria updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        promotionCriteriaService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Promotion criteria deleted successfully", null));
    }

    // --- Evaluation Endpoints ---

    @GetMapping("/evaluate/{employeeId}/{criteriaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<PromotionEvaluationResponse>> evaluateEmployee(
            @PathVariable Long employeeId,
            @PathVariable Long criteriaId) {
        PromotionEvaluationResponse response = promotionCriteriaService.evaluateEmployee(employeeId, criteriaId);
        return ResponseEntity.ok(ApiResponse.success("Promotion evaluation completed successfully", response));
    }

    @GetMapping("/evaluate/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<PromotionEvaluationResponse>>> evaluateEmployeeAgainstAll(
            @PathVariable Long employeeId) {
        List<PromotionEvaluationResponse> responses = promotionCriteriaService.evaluateEmployeeAgainstAll(employeeId);
        return ResponseEntity.ok(ApiResponse.success("Promotion evaluations completed successfully", responses));
    }

    @GetMapping("/my-evaluation/{criteriaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<PromotionEvaluationResponse>> evaluateMyEligibility(
            @PathVariable Long criteriaId,
            @AuthenticationPrincipal UserDetails userDetails) {
        PromotionEvaluationResponse response = promotionCriteriaService.evaluateMyEligibility(userDetails.getUsername(), criteriaId);
        return ResponseEntity.ok(ApiResponse.success("Your promotion evaluation completed successfully", response));
    }

    @GetMapping("/my-evaluations")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<PromotionEvaluationResponse>>> evaluateMyEligibilityAgainstAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<PromotionEvaluationResponse> responses = promotionCriteriaService.evaluateMyEligibilityAgainstAll(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Your promotion evaluations completed successfully", responses));
    }
}
