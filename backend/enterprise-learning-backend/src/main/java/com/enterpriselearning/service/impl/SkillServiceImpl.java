package com.enterpriselearning.service.impl;

import com.enterpriselearning.dto.request.SkillAssignRequest;
import com.enterpriselearning.dto.request.SkillRequest;
import com.enterpriselearning.dto.response.EmployeeSkillResponse;
import com.enterpriselearning.dto.response.SkillResponse;
import com.enterpriselearning.entity.EmployeeSkill;
import com.enterpriselearning.entity.Skill;
import com.enterpriselearning.entity.User;
import com.enterpriselearning.exception.BadRequestException;
import com.enterpriselearning.exception.ResourceNotFoundException;
import com.enterpriselearning.repository.EmployeeSkillRepository;
import com.enterpriselearning.repository.SkillRepository;
import com.enterpriselearning.repository.UserRepository;
import com.enterpriselearning.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final EmployeeSkillRepository employeeSkillRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SkillResponse createSkill(SkillRequest request) {
        if (skillRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Skill already exists: " + request.getName());
        }

        Skill skill = Skill.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .build();

        Skill savedSkill = skillRepository.save(skill);
        return mapToSkillResponse(savedSkill);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(this::mapToSkillResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SkillResponse getSkillById(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
        return mapToSkillResponse(skill);
    }

    @Override
    @Transactional
    public SkillResponse updateSkill(Long id, SkillRequest request) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));

        if (!skill.getName().equalsIgnoreCase(request.getName()) && skillRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Skill with name '" + request.getName() + "' already exists");
        }

        skill.setName(request.getName());
        skill.setDescription(request.getDescription());
        skill.setCategory(request.getCategory());

        Skill updatedSkill = skillRepository.save(skill);
        return mapToSkillResponse(updatedSkill);
    }

    @Override
    @Transactional
    public void deleteSkill(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
        
        List<EmployeeSkill> assignments = employeeSkillRepository.findBySkill(skill);
        employeeSkillRepository.deleteAll(assignments);
        skillRepository.delete(skill);
    }

    @Override
    @Transactional
    public EmployeeSkillResponse assignSkillToEmployee(SkillAssignRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + request.getUserId()));

        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + request.getSkillId()));

        Optional<EmployeeSkill> existing = employeeSkillRepository.findByUserAndSkill(user, skill);
        EmployeeSkill employeeSkill;

        if (existing.isPresent()) {
            employeeSkill = existing.get();
            employeeSkill.setProficiencyLevel(request.getProficiencyLevel());
            employeeSkill.setVerified(request.isVerified());
            employeeSkill.setYearsOfExperience(request.getYearsOfExperience());
        } else {
            employeeSkill = EmployeeSkill.builder()
                    .user(user)
                    .skill(skill)
                    .proficiencyLevel(request.getProficiencyLevel())
                    .verified(request.isVerified())
                    .yearsOfExperience(request.getYearsOfExperience())
                    .build();
        }

        EmployeeSkill saved = employeeSkillRepository.save(employeeSkill);
        return mapToEmployeeSkillResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeSkillResponse> getSkillsByEmployee(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + userId);
        }
        return employeeSkillRepository.findByUserId(userId).stream()
                .map(this::mapToEmployeeSkillResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeSkillResponse> getEmployeesBySkill(Long skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new ResourceNotFoundException("Skill not found with id: " + skillId);
        }
        return employeeSkillRepository.findBySkillId(skillId).stream()
                .map(this::mapToEmployeeSkillResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeSkillFromEmployee(Long userId, Long skillId) {
        EmployeeSkill employeeSkill = employeeSkillRepository.findByUserIdAndSkillId(userId, skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill assignment not found for employee " + userId + " and skill " + skillId));
        employeeSkillRepository.delete(employeeSkill);
    }

    private SkillResponse mapToSkillResponse(Skill skill) {
        long count = employeeSkillRepository.countBySkillId(skill.getId());
        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .description(skill.getDescription())
                .category(skill.getCategory())
                .employeeCount(count)
                .createdAt(skill.getCreatedAt())
                .updatedAt(skill.getUpdatedAt())
                .build();
    }

    private EmployeeSkillResponse mapToEmployeeSkillResponse(EmployeeSkill es) {
        return EmployeeSkillResponse.builder()
                .id(es.getId())
                .userId(es.getUser().getId())
                .employeeName(es.getUser().getFirstName() + " " + es.getUser().getLastName())
                .employeeId(es.getUser().getEmployeeId())
                .skillId(es.getSkill().getId())
                .skillName(es.getSkill().getName())
                .skillCategory(es.getSkill().getCategory())
                .proficiencyLevel(es.getProficiencyLevel())
                .verified(es.isVerified())
                .yearsOfExperience(es.getYearsOfExperience())
                .assignedDate(es.getAssignedDate())
                .build();
    }
}
