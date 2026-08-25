package com.enterpriselearning.controller;

import com.enterpriselearning.dto.request.SkillAssignRequest;
import com.enterpriselearning.dto.request.SkillRequest;
import com.enterpriselearning.dto.response.ApiResponse;
import com.enterpriselearning.dto.response.EmployeeSkillResponse;
import com.enterpriselearning.dto.response.SkillResponse;
import com.enterpriselearning.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<SkillResponse>> createSkill(@Valid @RequestBody SkillRequest request) {
        SkillResponse response = skillService.createSkill(request);
        return new ResponseEntity<>(ApiResponse.success("Skill created successfully", response), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SkillResponse>>> getAllSkills() {
        List<SkillResponse> skills = skillService.getAllSkills();
        return ResponseEntity.ok(ApiResponse.success("Skills retrieved successfully", skills));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillResponse>> getSkillById(@PathVariable Long id) {
        SkillResponse response = skillService.getSkillById(id);
        return ResponseEntity.ok(ApiResponse.success("Skill retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<SkillResponse>> updateSkill(
            @PathVariable Long id,
            @Valid @RequestBody SkillRequest request) {
        SkillResponse response = skillService.updateSkill(id, request);
        return ResponseEntity.ok(ApiResponse.success("Skill updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.ok(ApiResponse.success("Skill deleted successfully", null));
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<EmployeeSkillResponse>> assignSkillToEmployee(
            @Valid @RequestBody SkillAssignRequest request) {
        EmployeeSkillResponse response = skillService.assignSkillToEmployee(request);
        return ResponseEntity.ok(ApiResponse.success("Skill assigned successfully", response));
    }

    @GetMapping("/employee/{userId}")
    public ResponseEntity<ApiResponse<List<EmployeeSkillResponse>>> getSkillsByEmployee(@PathVariable Long userId) {
        List<EmployeeSkillResponse> skills = skillService.getSkillsByEmployee(userId);
        return ResponseEntity.ok(ApiResponse.success("Employee skills retrieved successfully", skills));
    }

    @GetMapping("/{id}/employees")
    public ResponseEntity<ApiResponse<List<EmployeeSkillResponse>>> getEmployeesBySkill(@PathVariable Long id) {
        List<EmployeeSkillResponse> responses = skillService.getEmployeesBySkill(id);
        return ResponseEntity.ok(ApiResponse.success("Skill employees retrieved successfully", responses));
    }

    @DeleteMapping("/employee/{userId}/{skillId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<Void>> removeSkillFromEmployee(
            @PathVariable Long userId,
            @PathVariable Long skillId) {
        skillService.removeSkillFromEmployee(userId, skillId);
        return ResponseEntity.ok(ApiResponse.success("Skill removed from employee successfully", null));
    }
}
