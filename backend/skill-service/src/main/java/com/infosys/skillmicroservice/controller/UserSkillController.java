package com.infosys.skillmicroservice.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.skillmicroservice.entity.UserSkill;
import com.infosys.skillmicroservice.service.UserSkillService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user-skills")
public class UserSkillController {

    private final UserSkillService userSkillService;

    public UserSkillController(UserSkillService userSkillService) {
        this.userSkillService = userSkillService;
    }

    @PostMapping
    public UserSkill addUserSkill(
            @Valid @RequestBody UserSkill userSkill) {

        return userSkillService.addUserSkill(userSkill);
    }

    @GetMapping
    public List<UserSkill> getAllUserSkills() {
        return userSkillService.getAllUserSkills();
    }

    @GetMapping("/{userSkillId}")
    public UserSkill getUserSkillById(
            @PathVariable UUID userSkillId) {

        return userSkillService.getUserSkillById(userSkillId);
    }

    @GetMapping("/user/{userId}")
    public List<UserSkill> getSkillsByUserId(
            @PathVariable UUID userId) {

        return userSkillService.getSkillsByUserId(userId);
    }

    @PutMapping("/{userSkillId}")
    public UserSkill updateUserSkill(
            @PathVariable UUID userSkillId,
            @Valid @RequestBody UserSkill userSkill) {

        return userSkillService.updateUserSkill(
                userSkillId,
                userSkill);
    }

    @DeleteMapping("/{userSkillId}")
    public String deleteUserSkill(
            @PathVariable UUID userSkillId) {

        userSkillService.deleteUserSkill(userSkillId);

        return "User skill deleted successfully";
    }
}