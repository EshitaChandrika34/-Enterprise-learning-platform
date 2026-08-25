package com.enterpriselearning.service;

import com.enterpriselearning.dto.request.SkillAssignRequest;
import com.enterpriselearning.dto.request.SkillRequest;
import com.enterpriselearning.dto.response.EmployeeSkillResponse;
import com.enterpriselearning.dto.response.SkillResponse;

import java.util.List;

public interface SkillService {
    SkillResponse createSkill(SkillRequest request);
    List<SkillResponse> getAllSkills();
    SkillResponse getSkillById(Long id);
    SkillResponse updateSkill(Long id, SkillRequest request);
    void deleteSkill(Long id);

    EmployeeSkillResponse assignSkillToEmployee(SkillAssignRequest request);
    List<EmployeeSkillResponse> getSkillsByEmployee(Long userId);
    List<EmployeeSkillResponse> getEmployeesBySkill(Long skillId);
    void removeSkillFromEmployee(Long userId, Long skillId);
}
