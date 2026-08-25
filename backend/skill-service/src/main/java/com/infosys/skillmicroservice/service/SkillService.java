package com.infosys.skillmicroservice.service;

import java.util.List;
import java.util.UUID;

import com.infosys.skillmicroservice.entity.Skill;

public interface SkillService {

    Skill addSkill(Skill skill);

    List<Skill> getAllSkills();

    Skill getSkillById(UUID skillId);

    Skill updateSkill(UUID skillId, Skill skill);

    void deleteSkill(UUID skillId);

}