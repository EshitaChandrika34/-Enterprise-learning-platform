package com.infosys.skillmicroservice.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.infosys.skillmicroservice.entity.Skill;
import com.infosys.skillmicroservice.repository.SkillRepository;
import com.infosys.skillmicroservice.service.SkillService;

@Service
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    public SkillServiceImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    public Skill addSkill(Skill skill) {
        return skillRepository.save(skill);
    }

    @Override
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    @Override
    public Skill getSkillById(UUID skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() ->
                        new RuntimeException("Skill not found with ID: " + skillId));
    }

    @Override
    public Skill updateSkill(UUID skillId, Skill newSkill) {

        Skill existingSkill = getSkillById(skillId);

        existingSkill.setSkillName(newSkill.getSkillName());
        existingSkill.setCategory(newSkill.getCategory());
        existingSkill.setDescription(newSkill.getDescription());

        return skillRepository.save(existingSkill);
    }

    @Override
    public void deleteSkill(UUID skillId) {

        Skill existingSkill = getSkillById(skillId);

        skillRepository.delete(existingSkill);
    }
}