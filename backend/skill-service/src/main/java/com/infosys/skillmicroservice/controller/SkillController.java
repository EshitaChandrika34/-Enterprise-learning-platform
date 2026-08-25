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

import com.infosys.skillmicroservice.entity.Skill;
import com.infosys.skillmicroservice.service.SkillService;
import org.springframework.web.bind.annotation.CrossOrigin;

import jakarta.validation.Valid;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping
    public Skill addSkill(@Valid @RequestBody Skill skill) {
        return skillService.addSkill(skill);
    }

    @GetMapping
    public List<Skill> getAllSkills() {
        return skillService.getAllSkills();
    }

    @GetMapping("/{skillId}")
    public Skill getSkillById(@PathVariable UUID skillId) {
        return skillService.getSkillById(skillId);
    }

    @PutMapping("/{skillId}")
    public Skill updateSkill(
            @PathVariable UUID skillId,
            @Valid @RequestBody Skill skill) {

        return skillService.updateSkill(skillId, skill);
    }

    @DeleteMapping("/{skillId}")
    public String deleteSkill(@PathVariable UUID skillId) {

        skillService.deleteSkill(skillId);

        return "Skill deleted successfully";
    }
}