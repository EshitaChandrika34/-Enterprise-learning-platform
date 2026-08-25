package com.enterpriselearning.controller;

import com.enterpriselearning.dto.request.CareerGoalRequest;
import com.enterpriselearning.dto.request.CareerPathRequest;
import com.enterpriselearning.dto.response.*;
import com.enterpriselearning.service.CareerService;
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
@RequestMapping("/api/career")
@RequiredArgsConstructor
public class CareerController {

    private final CareerService careerService;

    @PostMapping("/goals")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<CareerGoalResponse>> createCareerGoal(
            @Valid @RequestBody CareerGoalRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        CareerGoalResponse response = careerService.createCareerGoal(request, userDetails != null ? userDetails.getUsername() : null);
        return new ResponseEntity<>(ApiResponse.success("Career goal created successfully", response), HttpStatus.CREATED);
    }

    @GetMapping("/goals")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<CareerGoalResponse>>> getMyGoals(@AuthenticationPrincipal UserDetails userDetails) {
        List<CareerGoalResponse> goals = careerService.getMyGoals(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Career goals retrieved successfully", goals));
    }

    @GetMapping("/goals/employee/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<CareerGoalResponse>>> getGoalsByEmployee(@PathVariable Long userId) {
        List<CareerGoalResponse> goals = careerService.getGoalsByEmployee(userId);
        return ResponseEntity.ok(ApiResponse.success("Employee career goals retrieved successfully", goals));
    }

    @PutMapping("/goals/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<CareerGoalResponse>> updateCareerGoal(
            @PathVariable Long id,
            @Valid @RequestBody CareerGoalRequest request) {
        CareerGoalResponse response = careerService.updateCareerGoal(id, request);
        return ResponseEntity.ok(ApiResponse.success("Career goal updated successfully", response));
    }

    @DeleteMapping("/goals/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<Void>> deleteCareerGoal(@PathVariable Long id) {
        careerService.deleteCareerGoal(id);
        return ResponseEntity.ok(ApiResponse.success("Career goal deleted successfully", null));
    }

    @PostMapping("/paths")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<CareerPathResponse>> createCareerPath(@Valid @RequestBody CareerPathRequest request) {
        CareerPathResponse response = careerService.createCareerPath(request);
        return new ResponseEntity<>(ApiResponse.success("Career path created successfully", response), HttpStatus.CREATED);
    }

    @GetMapping("/paths")
    public ResponseEntity<ApiResponse<List<CareerPathResponse>>> getAllCareerPaths() {
        List<CareerPathResponse> paths = careerService.getAllCareerPaths();
        return ResponseEntity.ok(ApiResponse.success("Career paths retrieved successfully", paths));
    }

    @GetMapping("/paths/{id}")
    public ResponseEntity<ApiResponse<CareerPathResponse>> getCareerPathById(@PathVariable Long id) {
        CareerPathResponse response = careerService.getCareerPathById(id);
        return ResponseEntity.ok(ApiResponse.success("Career path retrieved successfully", response));
    }

    @GetMapping("/recommendations")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<SkillRecommendationResponse>> getMyRecommendations(
            @RequestParam(required = false) String targetRole,
            @AuthenticationPrincipal UserDetails userDetails) {
        SkillRecommendationResponse response = careerService.getMyRecommendations(userDetails.getUsername(), targetRole);
        return ResponseEntity.ok(ApiResponse.success("Skill recommendations generated successfully", response));
    }

    @GetMapping("/recommendations/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<SkillRecommendationResponse>> getRecommendationsForEmployee(
            @PathVariable Long userId,
            @RequestParam(required = false) String targetRole) {
        SkillRecommendationResponse response = careerService.getRecommendationsForEmployee(userId, targetRole);
        return ResponseEntity.ok(ApiResponse.success("Employee skill recommendations generated successfully", response));
    }

    // --- Career Progress Percentage Endpoints ---

    @GetMapping("/progress")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<CareerProgressResponse>> getMyCareerProgress(
            @RequestParam(required = false) Long pathId,
            @AuthenticationPrincipal UserDetails userDetails) {
        CareerProgressResponse response = careerService.calculateMyCareerProgress(userDetails.getUsername(), pathId);
        return ResponseEntity.ok(ApiResponse.success("Career readiness progress calculated successfully", response));
    }

    @GetMapping("/progress/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<CareerProgressResponse>>> getMyCareerProgressForAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<CareerProgressResponse> responses = careerService.getMyCareerProgressForAllGoals(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("All career readiness assessments retrieved successfully", responses));
    }

    @GetMapping("/progress/employee/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<CareerProgressResponse>> getEmployeeCareerProgress(
            @PathVariable Long userId,
            @RequestParam(required = false) Long pathId) {
        CareerProgressResponse response = careerService.calculateCareerProgress(userId, pathId);
        return ResponseEntity.ok(ApiResponse.success("Employee career readiness progress calculated successfully", response));
    }

    @GetMapping("/progress/employee/{userId}/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<List<CareerProgressResponse>>> getAllEmployeeCareerProgress(
            @PathVariable Long userId) {
        List<CareerProgressResponse> responses = careerService.getAllCareerProgressForEmployee(userId);
        return ResponseEntity.ok(ApiResponse.success("All employee career readiness assessments retrieved successfully", responses));
    }
}
